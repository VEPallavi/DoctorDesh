package com.doctordesh.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize


@Parcelize
class ProviderModel(
    var _id: String,
    var phoneNumber: String,
    var specialist: String,
    var firstName: String,
    var lastName: String,
    var providerStatus: String,
    var userStatus: String,
    var profilePic: String,
    var deviceToken: String
) : Parcelable


