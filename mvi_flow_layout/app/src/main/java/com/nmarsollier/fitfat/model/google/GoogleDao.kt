package com.nmarsollier.fitfat.model.google

import android.content.Intent
import androidx.fragment.app.Fragment
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.nmarsollier.fitfat.R


object GoogleDao {
    internal fun getLoginIntent(fragment: Fragment): Intent {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(fragment.getString(R.string.web_client_id))
            .build()

        return GoogleSignIn.getClient(
            fragment.requireActivity(),
            gso
        ).signInIntent
    }
}
