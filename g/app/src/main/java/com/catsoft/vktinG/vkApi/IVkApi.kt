package com.catsoft.vktinG.vkApi

import com.catsoft.vktinG.vkApi.model.VKGroup
import com.catsoft.vktinG.vkApi.model.VKProduct
import io.reactivex.Observable

interface IVkApi {
    fun getMarketsList(id: Int): Observable<List<VKGroup>>

    fun getProductsList(idMarket: Int): Observable<List<VKProduct>>

    fun addProductToFavorite(ownerId: Int, idProduct: Int): Observable<Int>

    fun removeProductFromFavorite(ownerId: Int, idProduct: Int): Observable<Int>

    fun isLikedProduct(ownerId: Int, idProduct: Int): Observable<Boolean>
}