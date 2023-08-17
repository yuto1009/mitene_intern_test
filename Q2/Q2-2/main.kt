import kotlin.math.ceil
import java.text.SimpleDateFormat

// Parking lot capacity and license plate format
const val CAPACITY = 3
const val LICENSE_PLATE_LENGTH = 7


// Time constants
const val MILLISECONDS_IN_A_SECOND = 1000
const val SECONDS_IN_A_MINUTE = 60
const val MINUTES_IN_AN_HOUR = 60.0
const val MILLISECONDS_IN_AN_HOUR = MILLISECONDS_IN_A_SECOND * SECONDS_IN_A_MINUTE * MINUTES_IN_AN_HOUR

// Fee constants
const val HOURS_IN_HALF_DAY = 12
const val FEE_PER_HALF_DAY = 2000
const val HOURLY_FEE = 300

data class Car(val number: String)

class ParkingLot(private val capacity: Int = CAPACITY) 
{
    private val parkedCars = mutableMapOf<Car, Long>()

    fun park(car: Car, timestamp: Long): Boolean 
    {
        if (isParkingLotFull() || isCarAlreadyParked(car)) 
        {
            return false
        }
        parkedCars[car] = timestamp
        return true
    }

    fun depart(car: Car, timestamp: Long): Int? 
    {
        val startTime = parkedCars.remove(car) ?: return null
        val parkedDurationInHours = ceil((timestamp - startTime).toDouble() / MILLISECONDS_IN_AN_HOUR).toInt()
        return calculateFee(parkedDurationInHours)
    }

    private fun isParkingLotFull(): Boolean
    {
        return parkedCars.size >= capacity
    }

    fun isCarAlreadyParked(car: Car): Boolean
    {
        return parkedCars.containsKey(car)
    }

    private fun calculateFee(hours: Int): Int 
    {
        return (hours / HOURS_IN_HALF_DAY * FEE_PER_HALF_DAY) + minOf(HOURLY_FEE * (hours % HOURS_IN_HALF_DAY), FEE_PER_HALF_DAY)
    }
}

fun String.isValidFormat(): Boolean 
{
    if (this.length != LICENSE_PLATE_LENGTH)
    {
        return false
    }
    for (char in this) 
    {
        if (!char.isDigit() && !char.isUpperCase())
        {
            return false
        }
    }
    return true
}

fun main() {
    val commands = listOf(
        "2023/07/04 11:12 [1234ABC] Park",
        "2023/07/04 12:34 [282E98Z] Park",
        "2023/07/04 13:45 [3EZKNE8] Park",
        "2023/07/04 13:45 [49LKB9D] Park",
        "2023/07/04 14:42 [1234ABC] Depart"
    )

    val parkingLot = ParkingLot()
    val sdf = SimpleDateFormat("yyyy/MM/dd HH:mm")

    for (command in commands) 
    {
        val parts = command.split(" ")
        val timestamp = sdf.parse("${parts[0]} ${parts[1]}").time
        val licensePlate = parts[2].removeSurrounding("[", "]")
        val action = parts[3]

        when (action) 
        {
            "Park" -> 
            {
                if (licensePlate.isValidFormat()) 
                {
                    if (parkingLot.isCarAlreadyParked(Car(licensePlate))) 
                    {
                        println("$command - Car is already parked.")
                    } 
                    else if (!parkingLot.park(Car(licensePlate), timestamp)) 
                    {
                        println("$command - Parking lot is full.")
                    } 
                    else 
                    {
                        println("$command - Car parked.")
                    }
                } 
                else 
                {
                    println("$command - Invalid license plate format.")
                }
            }
            "Depart" -> 
            {
                val fee = parkingLot.depart(Car(licensePlate), timestamp)
                if (fee == null) 
                {
                    println("$command - Car not found.")
                } 
                else 
                {
                    println("$command - Car departed. Fee: $fee")
                }
            }
            else -> 
            {
                println("$command - Invalid command.")
            }
        }
    }
}