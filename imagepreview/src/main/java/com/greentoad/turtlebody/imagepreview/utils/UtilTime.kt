package com.greentoad.turtlebody.imagepreview.utils

import java.util.concurrent.TimeUnit


/**
 * Created by niraj on 15-10-2018.
 */
object UtilTime {

    @JvmStatic
    fun timeFormatted(millis: Long): String {
        return if (millis > 3600000L) {
            String.format(
                "%02d:%02d:%02d",
                TimeUnit.MILLISECONDS.toHours(millis),
                TimeUnit.MILLISECONDS.toMinutes(millis) - TimeUnit.HOURS.toMinutes(
                    TimeUnit.MILLISECONDS.toHours(
                        millis
                    )
                ), // The change is in this line
                TimeUnit.MILLISECONDS.toSeconds(millis) - TimeUnit.MINUTES.toSeconds(
                    TimeUnit.MILLISECONDS.toMinutes(
                        millis
                    )
                )
            )
        } else {
            String.format(
                "%02d:%02d"
                ,
                TimeUnit.MILLISECONDS.toMinutes(millis) - TimeUnit.HOURS.toMinutes(
                    TimeUnit.MILLISECONDS.toHours(millis)
                )
                ,
                TimeUnit.MILLISECONDS.toSeconds(millis) - TimeUnit.MINUTES.toSeconds(
                    TimeUnit.MILLISECONDS.toMinutes(millis)
                )
            )
        }
    }
}