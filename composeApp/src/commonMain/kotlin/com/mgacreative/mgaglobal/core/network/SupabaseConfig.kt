package com.mgacreative.mgaglobal.core.network

import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.storage.Storage
import io.github.jan.supabase.realtime.Realtime

object SupabaseConfig {
    const val SUPABASE_URL = "https://xwolkximbkrhrqocvuyp.supabase.co"
    const val SUPABASE_ANON_KEY = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6Inh3b2xreGltYmtyaHJxb2N2dXlwIiwicm9sZSI6ImFub24iLCJpYXQiOjE3ODEzNDA4ODAsImV4cCI6MjA5NjkxNjg4MH0.-LgLJvwpExzjhxesSk4LFVblCC5AjXWehzHlHe3-T4g"

    val client by lazy {
        if (SUPABASE_URL.isEmpty() || SUPABASE_ANON_KEY.isEmpty()) {
            null
        } else {
            createSupabaseClient(
                supabaseUrl = SUPABASE_URL,
                supabaseKey = SUPABASE_ANON_KEY
            ) {
                install(Postgrest)
                install(Auth)
                install(Storage)
                install(Realtime)
            }
        }
    }
}
