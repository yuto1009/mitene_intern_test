import java.io.File

fun main() 
{
    val lines = File("words.txt").readLines()
    val duplicated = lines.groupBy { it }.filterValues { it.size > 1 }.keys
    
    for (word in duplicated)
    {
        println(word)
    }
}