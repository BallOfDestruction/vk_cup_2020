package com.catsoft.vktinE.vkApi

import android.net.Uri
import com.catsoft.vktinE.vkApi.requests.VKWallPostCommand
import com.vk.api.sdk.VK
import io.reactivex.Observable

class VKWallApi : IVKWallApi {
    override fun post(string: String, images: List<Uri>): Observable<Int> = Observable.create<Int> {
        VK.execute(VKWallPostCommand(string, images, 141454621), VKApiEmittedCallback<Int>(it))
    }
}
