package com.senjapagi.meetup.Preference

object URL {
    const val ROOT = "http://103.253.27.125:10000/booking/"
    const val ROOT_ALT = "http://103.253.27.125:10000/booking/index.php"
    const val BASE_URL = "${ROOT}index.php?/"


    const val USER_DATA = "${ROOT}index.php?/api/user/profile"


    //api/user/daftarpemesanan/{order_id}/delete
//    http://103.253.27.125:10000/booking/assets/upload/roomphotos/
//    http://103.253.27.125:10000/booking/assets/uploads/userphotos/21594565239.png

    const val ROOM_THUMBNAIL = "${ROOT}assets/uploads/thumbnails/"
    const val USER_THUMBNAIL = "${ROOT}assets/uploads/userphotos/"
    const val ROOM_PHOTOS = "${ROOT}assets/uploads/roomphotos/"
//    http://103.253.27.125:10000/booking/index.php?/api/user/ruang/tersedia
    const val LOGIN = "${BASE_URL}api/login"

    const val ROOM_ALL = "${BASE_URL}api/admin/ruang"

    const val PLACE_BOOKING = "${BASE_URL}api/user/pesan"


    const val ROOM_AVAIL = "http://103.253.27.125:10000/booking/index.php?/api/user/ruang/tersedia"
    const val ROOM_DETAIL = "${BASE_URL}/api/admin/ruang/{room_id}"

    const val USER_RATE_ORDER = "http://103.253.27.125:10000/booking/index.php?/api/user/daftarpemesanan/"

    const val ORDER_LIST_BY_USER = "http://103.253.27.125:10000/booking/index.php?/api/user/daftarpemesanan"

//http://103.253.27.125:10000/booking/index.php?/api/user/daftarpemesanan
    const val USER_DETAIL = "${BASE_URL}/api/admin/pengguna/"
//
}