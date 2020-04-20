package com.google.firebase.ml.md.kotlin

import com.google.firebase.ml.md.kotlin.models.itempost

class DataSource{

    companion object{
        fun createDataSet(): ArrayList<itempost>{

            val list = ArrayList<itempost>()

            list.add(

                    itempost(

                            "Oyster Bake Tickets",//title

                            "You made it to the end of the course!\r\n\r\nNext we'll be building the REST API!",//future improvemnts

                            "https://raw.githubusercontent.com/tommyzam/Re-cash-1/master/oyster_bake_2020.png",//images loaded from internet

                            "50 Points"//point value

                    )

            )

            list.add(

                    itempost(

                            "Starbucks Gift card",

                            "https://raw.githubusercontent.com/tommyzam/Re-cash-1/master/starbucks_card.jpg",

                            "https://raw.githubusercontent.com/tommyzam/Re-cash-1/master/starbucks_card.jpg",

                            "100 Points"

                    )

            )



            list.add(

                    itempost(

                            "St. Marys T-Shirt",

                            "",

                            "https://raw.githubusercontent.com/tommyzam/Re-cash-1/master/stmarystshirt.png",

                            "200 Points"

                    )

            )

            list.add(

                    itempost(

                            "St Mary's Hat",

                            "",

                            "https://raw.githubusercontent.com/tommyzam/Re-cash-1/master/stmaryshat.jpg",

                            "250 Points"

                    )

            )





            return list

        }

    }

}