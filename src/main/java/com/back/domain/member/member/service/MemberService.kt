package com.back.domain.member.member.service

import com.back.domain.api.service.ApiKeyService
import com.back.domain.auth.service.AuthService
import com.back.domain.club.club.repository.ClubRepository
import com.back.domain.club.clubMember.entity.ClubMember
import com.back.domain.club.clubMember.repository.ClubMemberRepository
import com.back.domain.member.member.dto.request.GuestDto
import com.back.domain.member.member.dto.request.MemberLoginDto
import com.back.domain.member.member.dto.request.MemberRegisterDto
import com.back.domain.member.member.dto.request.UpdateMemberInfoDto
import com.back.domain.member.member.dto.response.*
import com.back.domain.member.member.entity.Member
import com.back.domain.member.member.entity.Member.Companion.createGuest
import com.back.domain.member.member.entity.Member.Companion.createMember
import com.back.domain.member.member.entity.MemberInfo
import com.back.domain.member.member.repository.MemberInfoRepository
import com.back.domain.member.member.repository.MemberRepository
import com.back.global.aws.S3Service
import com.back.global.enums.ClubMemberRole
import com.back.global.enums.ClubMemberState
import com.back.global.exception.ServiceException
import jakarta.validation.Valid
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.multipart.MultipartFile
import java.io.IOException
import java.util.*

@Service
@Transactional(readOnly = true)
class MemberService(
    private val memberRepository: MemberRepository,
    private val memberInfoRepository: MemberInfoRepository,
    private val apiKeyService: ApiKeyService,
    private val authService: AuthService,
    private val s3Service: S3Service,
    private val clubRepository: ClubRepository,
    private val clubMemberRepository: ClubMemberRepository,
    private val passwordEncoder: PasswordEncoder
) {

    // ============================== [회원] 회원가입 ==============================
    @Transactional
    fun registerMember(dto: MemberRegisterDto): MemberAuthResponse {
        validateDuplicateMember(dto)

        val tag = generateMemberTag(dto.nickname)
        val apiKey = apiKeyService.generateApiKey()

        val member = createAndSaveMember(dto, tag)
        createAndSaveMemberInfo(dto, member, apiKey)

        val accessToken = generateAccessToken(member)
        return MemberAuthResponse(apiKey, accessToken)
    }

    // ============================== [비회원] 모임 가입 ==============================
    @Transactional
    fun registerGuestMember(@Valid dto: GuestDto): GuestResponse {
        validateDuplicateGuest(dto)

        val tag = generateMemberTag(dto.nickname)
        val guest = createAndSaveGuestMember(dto, tag)

        val club = clubRepository.findById(dto.clubId)
            .orElseThrow { ServiceException(400, "클럽을 찾을 수 없습니다.") }

        // 임시방편용 코드. clubMember 코틀린 전환 후 개변 예정
//        val clubMember = ClubMember()
//        clubMember.member = guest
//        clubMember.club = club
//        clubMember.role = ClubMemberRole.PARTICIPANT
//        clubMember.state = ClubMemberState.APPLYING
//
//        clubMemberRepository.save(clubMember)

        val accessToken = generateAccessToken(guest)
        return GuestResponse(dto.nickname, accessToken, dto.clubId)
    }

    // ============================== [회원] 로그인 ==============================
    fun loginMember(@Valid dto: MemberLoginDto): MemberAuthResponse {
        val memberInfo = memberInfoRepository.findByEmail(dto.email)
            .orElseThrow { ServiceException(400, "해당 사용자를 찾을 수 없습니다.") }

        val member = memberInfo.getMember()
            ?: throw ServiceException(400, "해당 사용자를 찾을 수 없습니다.")

        validatePassword(dto.password, member)

        val apiKey = member.getMemberInfo()?.apiKey
            ?: throw ServiceException(400, "api키가 존재하지 않습니다..")
        val accessToken = authService.generateAccessToken(member)

        return MemberAuthResponse(apiKey, accessToken)
    }

    // ============================== [비회원] 임시 로그인 ==============================
    fun loginGuestMember(@Valid dto: GuestDto): GuestResponse {
        val member = memberRepository.findByGuestNicknameInClub(dto.nickname, dto.clubId)
            .orElseThrow { ServiceException(400, "해당 사용자를 찾을 수 없습니다.") }

        validatePassword(dto.password, member)

        val accessToken = authService.generateAccessToken(member)
        return GuestResponse(dto.nickname, accessToken, dto.clubId)
    }

    // ============================== [회원] 탈퇴 ==============================
    @Transactional
    fun withdrawMember(nickname: String, tag: String): MemberWithdrawMembershipResponse {
        val member = findMemberByNicknameAndTag(nickname, tag)
        deleteMember(member)
        return MemberWithdrawMembershipResponse(member.nickname, member.tag ?: "")
    }

    // ============================== [회원] 정보 조회/수정 ==============================
    fun getMemberInfo(id: Long): MemberDetailInfoResponse {
        val member: Member = findMemberById(id)
            .orElseThrow { ServiceException(400, "해당 id의 유저가 없습니다.") }
        val info: MemberInfo = member.getMemberInfo()
            ?: throw ServiceException(400, "해당 유저의 정보가 없습니다.")

        return MemberDetailInfoResponse(
            member.nickname,
            info.email!!,
            info.bio,
            info.profileImageUrl,
            member.tag
        )
    }

    @Transactional
    fun updateMemberInfo(id: Long, dto: UpdateMemberInfoDto, image: MultipartFile?): MemberDetailInfoResponse {
        val member = findMemberById(id)
            .orElseThrow { ServiceException(400, "해당 id의 유저가 없습니다.") }
        val info = member.getMemberInfo()
            ?: throw ServiceException(400, "해당 유저의 정보가 없습니다.")

        val password = dto.password?.takeIf { it.isNotBlank() }?.let { passwordEncoder.encode(it) } ?: member.password
        val nickname = dto.nickname ?: member.nickname
        val tag = if (dto.nickname != null) generateMemberTag(dto.nickname) else member.tag
        val bio = dto.bio ?: info.bio

        member.updateInfo(nickname, tag, password)
        info.updateBio(bio)

        if (image != null && !image.isEmpty) {
            val contentType = image.contentType ?: ""
            if (!contentType.startsWith("image/")) throw ServiceException(400, "이미지 파일만 업로드 가능합니다.")
            try {
                val imageUrl = s3Service.upload(image, "member/${info.id}/profile")
                info.updateImageUrl(imageUrl)
            } catch (e: IOException) {
                throw ServiceException(400, "이미지 업로드 중 오류가 발생했습니다.")
            }
        }

        return MemberDetailInfoResponse(
            member.nickname,
            info.email!!,
            info.bio,
            info.profileImageUrl,
            member.tag
        )
    }

    // ============================== [검증 메소드] ==============================
    private fun validateDuplicateMember(dto: MemberRegisterDto) {
        val email = dto.email.lowercase(Locale.getDefault())
        if (memberInfoRepository.findByEmail(email).isPresent) {
            throw ServiceException(400, "이미 사용 중인 이메일입니다.")
        }
    }

    private fun validateDuplicateGuest(dto: GuestDto) {
        if (memberRepository.existsGuestNicknameInClub(dto.nickname, dto.clubId)) {
            throw ServiceException(400, "이미 사용 중인 닉네임입니다.")
        }
    }

    private fun validatePassword(password: String, member: Member) {
        if (!passwordEncoder.matches(password, member.password)) {
            throw ServiceException(400, "해당 사용자를 찾을 수 없습니다.")
        }
    }

    // ============================== [생성 메소드] ==============================
    private fun generateMemberTag(nickname: String): String {
        var tag: String
        do {
            tag = UUID.randomUUID().toString().substring(0, 6)
        } while (memberRepository.existsByNicknameAndTag(nickname, tag))
        return tag
    }

    private fun createAndSaveMember(dto: MemberRegisterDto, tag: String): Member {
        val hashedPassword = passwordEncoder.encode(dto.password)
        val member = createMember(dto.nickname, hashedPassword, tag)
        return memberRepository.save(member)
    }

    private fun createAndSaveGuestMember(dto: GuestDto, tag: String): Member {
        val hashedPassword = passwordEncoder.encode(dto.password)
        val guest = createGuest(dto.nickname, hashedPassword, tag)
        return memberRepository.save(guest)
    }

    private fun createAndSaveMemberInfo(dto: MemberRegisterDto, member: Member, apiKey: String): MemberInfo {
        val info = MemberInfo(
            email = dto.email,
            bio = dto.bio,
            profileImageUrl = "",
            apiKey = apiKey,
            member = member
        )
        val savedInfo = memberInfoRepository.save(info)
        member.setMemberInfo(savedInfo)
        return savedInfo
    }

    // ============================== [유틸 / 기타] ==============================
    fun checkPasswordValidity(memberId: Long, password: String): MemberPasswordResponse {
        val member = findMemberById(memberId)
            .orElseThrow { ServiceException(400, "해당 id로 유저가 없습니다.") }
        return try {
            validatePassword(password, member)
            MemberPasswordResponse(true)
        } catch (e: ServiceException) {
            MemberPasswordResponse(false)
        }
    }

    fun payload(accessToken: String) = authService.payload(accessToken)

    fun findMemberByEmail(email: String) = memberInfoRepository.findByEmail(email)
        .orElseThrow { ServiceException(400, "사용자를 찾을 수 없습니다.") }
        .getMember()

    private fun deleteMember(member: Member) = memberRepository.delete(member)

    fun findMemberByNicknameAndTag(nickname: String, tag: String) =
        memberRepository.findByNicknameAndTag(nickname, tag)
            .orElseThrow { ServiceException(400, "회원 정보를 찾을 수 없습니다.") }

    fun findMemberById(id: Long) = memberRepository.findById(id)

    fun getMember(memberId: Long) =
        findMemberById(memberId).orElseThrow { NoSuchElementException("멤버가 존재하지 않습니다.") }

    fun generateAccessToken(member: Member) = authService.generateAccessToken(member)

    fun findMemberByApiKey(apiKey: String) =
        memberInfoRepository.findByApiKey(apiKey)
            .orElseThrow { ServiceException(400, "유효하지 않은 Refresh Token 입니다.") }
            .getMember()
}
