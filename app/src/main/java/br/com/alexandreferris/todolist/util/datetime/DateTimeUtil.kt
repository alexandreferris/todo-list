package br.com.alexandreferris.todolist.util.datetime

open class DateTimeUtil {

    companion object {
        /**
         * make double format hour:minute string
         * @hour : 1
         * @minute : 2
         * return : 01:02
         */
        fun addLeadingZeroToTime(hour: Int, minutes: Int): String {
            val hourZero = if (hour >= 10) Integer.toString(hour) else String.format("0%s", Integer.toString(hour))
            val minuteZero = if (minutes >= 10) Integer.toString(minutes) else String.format("0%s", Integer.toString(minutes))
            return "$hourZero:$minuteZero"
        }

        /**
         * adds plus one in month to get the current month
         * @day : 1
         * @month : 2
         * @year : 2000
         * return : 01/02/2000
         */
        fun correctDayAndMonth(day: Int, month: Int, year: Int): String {
            val dayZero = if (day >= 10) Integer.toString(day) else String.format("0%s", Integer.toString(day))
            var monthZero = if (month >= 10) Integer.toString(month + 1) else String.format("0%s", Integer.toString(month + 1))

            return "$dayZero/$monthZero/$year"
        }
    }
}