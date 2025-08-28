package com.back.domain.member.member.entity

import jakarta.persistence.*

@Entity
class MemberInfo(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(updatable = false)
    var id: Long? = null,

    @Column(unique = true)
    var email: String? = null,

    var bio: String? = null,

    var profileImageUrl: String? = null,

    var apiKey: String? = null,

    @OneToOne
    @JoinColumn(name = "member_id", unique = true)
    private var member: Member? = null // backing property
) {

    //===============================컴패니언 객체 (Builder 대체)==========================
    companion object {
        fun create(
            email: String?,
            bio: String?,
            profileImageUrl: String?,
            member: Member?,
            apiKey: String?
        ): MemberInfo {
            return MemberInfo(
                email = email,
                bio = bio,
                profileImageUrl = profileImageUrl,
                member = member,
                apiKey = apiKey
            )
        }
    }

    //===========================커스텀 getter/setter=======================
    fun setMember(member: Member?) {
        this.member = member
    }

    fun getMember(): Member? = member

    fun updateImageUrl(imageUrl: String?) {
        if (imageUrl != null) this.profileImageUrl = imageUrl
    }

    fun updateBio(bio: String?) {
        if (bio != null) this.bio = bio
    }
}
