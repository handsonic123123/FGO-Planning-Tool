package com.ssttkkl.fgoplanningtool.resources

object ConstantValues {
    val ascensionQP: List<List<Long>> = listOf<List<Long>>(
            listOf(15000, 45000, 150000, 450000,
                    600000, 800000, 1000000, 2000000, 3000000, 4000000, 5000000, 6000000, 7000000, 8000000),
            listOf(10000, 30000, 90000, 300000,
                    400000, 600000, 800000, 1000000, 2000000, 3000000, 4000000, 5000000, 6000000, 7000000),
            listOf(15000, 45000, 150000, 450000,
                    600000, 800000, 1000000, 2000000, 3000000, 4000000, 5000000, 6000000, 7000000, 8000000),
            listOf(30000, 100000, 300000, 900000,
                    1000000, 2000000, 3000000, 4000000, 5000000, 6000000, 7000000, 8000000, 9000000),
            listOf(50000, 150000, 500000, 1500000,
                    4000000, 5000000, 6000000, 7000000, 8000000, 9000000, 10000000),
            listOf(100000, 300000, 1000000, 3000000,
                    9000000, 10000000, 11000000, 12000000, 13000000)
    )

    val skillQP: List<List<Long>> = listOf<List<Long>>(
            listOf(20000, 40000, 120000, 160000, 400000, 500000, 1000000, 1200000, 2000000),
            listOf(10000, 20000, 60000, 80000, 200000, 250000, 500000, 600000, 1000000),
            listOf(20000, 40000, 120000, 160000, 400000, 500000, 1000000, 1200000, 2000000),
            listOf(50000, 100000, 300000, 400000, 1000000, 1250000, 2500000, 3000000, 5000000),
            listOf(100000, 200000, 600000, 800000, 2000000, 2500000, 5000000, 6000000, 10000000),
            listOf(200000, 400000, 1200000, 1600000, 4000000, 5000000, 10000000, 12000000, 20000000))

    val dressQP: Long = 3000000

    val expCardQP: List<Pair<Long, Long>> = listOf<Pair<Long, Long>>(
            Pair(105, 45),
            Pair(70, 30),
            Pair(105, 45),
            Pair(140, 60),
            Pair(280, 120),
            Pair(420, 180))

    val stageMapToMaxLevel: List<List<Int>> = listOf(
            listOf(25, 35, 45, 55, 65, 70, 75, 80, 85, 90, 92, 94, 96, 98, 100),
            listOf(20, 30, 40, 50, 60, 70, 75, 80, 85, 90, 92, 94, 96, 98, 100),
            listOf(25, 35, 45, 55, 65, 70, 75, 80, 85, 90, 92, 94, 96, 98, 100),
            listOf(30, 40, 50, 60, 70, 75, 80, 85, 90, 92, 94, 96, 98, 100),
            listOf(40, 50, 60, 70, 80, 85, 90, 92, 94, 96, 98, 100),
            listOf(50, 60, 70, 80, 90, 92, 94, 96, 98, 100))

    val nextLevelExp: List<Int> = listOf(0,
            100, 300, 600, 1000, 1500, 2100, 2800, 3600, 4500, 5500,
            6600, 7800, 9100, 10500, 12000, 13600, 15300, 17100, 19000, 21000,
            23100, 25300, 27600, 30000, 32500, 35100, 37800, 40600, 43500, 46500,
            49600, 52800, 56100, 59500, 63000, 66600, 70300, 74100, 78000, 82000,
            86100, 90300, 94600, 99000, 103500, 108100, 112800, 117600, 122500, 127500,
            132600, 137800, 143100, 148500, 154000, 159600, 165300, 171100, 177000, 183000,
            189100, 195300, 201600, 208000, 214500, 221100, 227800, 234600, 241500, 248500,
            255600, 262800, 270100, 277500, 285000, 292600, 300300, 308100, 316000, 324000,
            332100, 340300, 348600, 357000, 365500, 374100, 382800, 391600, 400500, 418500,
            454900, 510100, 584500, 678500, 792500, 926900, 1082100, 1258500, 1456500)

    val perCardExp: Int = 32400

    fun getLevel(exp: Int): Int {
        var tot = 0L
        for (i in 1 until nextLevelExp.size) {
            if (tot + nextLevelExp[i] <= exp)
                tot += nextLevelExp[i]
            else
                return i
        }
        return 100
    }

    fun getStage(star: Int, level: Int): Int {
        return stageMapToMaxLevel[star].indexOfFirst { it >= level }
    }

    fun getExp(level: Int): Int {
        return if (level == 0) 0 else nextLevelExp.slice(0 until level).sum()
    }
}