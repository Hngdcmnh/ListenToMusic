package com.example.listentomusic.model


import java.io.Serializable

/**
 * data class Music
 *
 *@param name: music name
 *@param artist: music artist
 *@param link: music link from storage
 *@param album: music album
 */
data class Music(
    val name: String?,
    val artist: String,
    val link: String,
    val album: String
) : Serializable
