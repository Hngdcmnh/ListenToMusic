package gst.tranningcourse.musicplayermockproject.model

/**
 * data class Album
 *
 *@param id for Album id
 *@param albumName for Name of album
 *@param artistName for name of artist
 *@param albumImg for path to get album image
 *@param nr_of_song for number of Songs
 */
data class Album(
    var id : Long,
    var albumName: String,
    var artistName: String,
    var albumImg: String? = null ,
    var nr_of_songs: Int
)