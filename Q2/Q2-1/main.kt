import kotlin.math.ceil

// Parking lot actions and its capacity
const val ABORT = "Abort"
const val PARK = "Park"
const val DEPART = "Depart"
const val CAPACITY = 3

// License plate format
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

    fun park(car: Car): Boolean 
    {
        if (isParkingLotFull() || isCarAlreadyParked(car)) 
        {
            return false 
        }
        parkedCars[car] = System.currentTimeMillis()
        return true
    }
    fun depart(car: Car): Int? 
    {
        val startTime = parkedCars.remove(car) ?: return null

        val parkedDurationInHours = ceil((System.currentTimeMillis() - startTime) / MILLISECONDS_IN_AN_HOUR).toInt()
        return calculateFee(parkedDurationInHours)
    }
    fun displayParkedCars() 
    {
        if (parkedCars.isEmpty()) 
        {
            println("Currently no cars in the parking lot.")
            return
        }
        val licensePlates = parkedCars.keys.joinToString(separator = ", ") { car -> car.number }
        println("Cars in the parking lot: $licensePlates")
    }
    fun isCarAlreadyParked(car: Car): Boolean 
    {
        return parkedCars.containsKey(car)
    }
    private fun isParkingLotFull(): Boolean
    {
        return parkedCars.size >= capacity
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

fun putInstruction(parkingLot: ParkingLot)
{
    println()
    println("Park <license plate>")
    println("Depart <license plate>")
    println("or Abort")
    parkingLot.displayParkedCars()
    println()
}

fun main()
{
    val parkingLot = ParkingLot()
    var input: String? 

    do
    {
        putInstruction(parkingLot)
        input = readLine()

        val order = input?.split(" ")?.firstOrNull()
        val licensePlate = input?.split(" ")?.lastOrNull()

        if (licensePlate == null || !licensePlate.isValidFormat()) 
        {
            println("Invalid license plate format")
        } 
        else 
        {
            when (order) 
            {
                PARK -> 
                {
                    if (parkingLot.isCarAlreadyParked(Car(licensePlate)))
                    {
                        println("Car is already parked.")
                    } 
                    else if (!parkingLot.park(Car(licensePlate)))
                    {
                        println("Parking lot is full.")
                    }
                    else 
                    {
                        println("Car parked")
                    }
                }
                DEPART -> 
                {
                    val fee = parkingLot.depart(Car(licensePlate))
                    if (fee == null) 
                    {
                        println("Car not found")
                    } 
                    else 
                    {
                        println("Car departed. Fee: $fee")
                    }
                }
                else -> 
                {
                    println("Invalid order")
                }
            }
        }
    } while (input != ABORT)
}


