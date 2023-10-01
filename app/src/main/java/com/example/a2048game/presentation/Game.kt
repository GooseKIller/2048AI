import kotlin.random.Random

class Game(private var size:Int=8) {
    private var board = Array(size) { Array(size) {0} }
    private var step = Random.nextInt(5)
    private var randomSeeds: RandomSeeds = RandomSeeds()

    fun getStep(): Int {
        return step
    }

    fun clearBoard(){
        this.board = Array(size) { Array(size) {0} }
        newTiles()
        newTiles()
    }

    fun getBoard(): Array<Array<Int>> {
        return this.board
    }

    fun getSeeds():RandomSeeds{
        return this.randomSeeds
    }

    fun getMaxCell(): Int{
        return this.notEmptyCell().maxBy { it.value }.value
    }

    fun getRecord(): Int{
        return this.notEmptyCell().sumOf { it.value }
    }

    private fun pseudoRandomInt(number: Int): Int {
        // Простой пример на основе хеширования
        val seed = this.randomSeeds.seed//42 // Начальное значение семени
        val multiplier =
            this.randomSeeds.multiplier * (number % 8) // Множитель (Раньше здесь было 6)
        val modulus = this.randomSeeds.modules // Модуль (раньше 97)

        // Применяем хеш-функцию к значению `a`

        return (number * multiplier + seed) % modulus
    }

    fun isLose(): Boolean {
        val cells = this.notEmptyCell()
        if (cells.size != this.size * this.size) {
            return false
        }
        for (x in 0 until size) {
            for (y in 0 until size) {
                val currentCell = cells[x * size + y]

                // Проверяем соседнюю ячейку справа
                if (x < size - 1) {
                    val rightCell = cells[(x + 1) * size + y]
                    if (currentCell.value == rightCell.value) {
                        return false
                    }
                }

                // Проверяем соседнюю ячейку снизу
                if (y < size - 1) {
                    val downCell = cells[x * size + (y + 1)]
                    if (currentCell.value == downCell.value) {
                        return false
                    }
                }
            }
        }
        return true
    }

    private fun emptyCell(): MutableList<Cell>{
        val cells = mutableListOf<Cell>()
        for (i in board.indices){
            for (j in board.indices) {
                if (board[i][j] == 0){
                    cells.add(Cell(i, j))
                }
            }
        }
        return cells
    }

    private fun notEmptyCell(): MutableList<Cell>{
        val cells = mutableListOf<Cell>()
        for (i in board.indices){
            for (j in board.indices) {
                if (board[i][j] != 0){
                    cells.add(Cell(i, j, board[i][j]))
                }
            }
        }
        return cells
    }

    private fun newTiles() {
        val emptyCells = this.emptyCell()
        if (emptyCells.size == 0) {
            return
        }

        var newTile = 2
        if (this.step % 10 == 0) {
            newTile = 4
        }
        val cell: Cell = emptyCells[this.pseudoRandomInt(step)%emptyCells.size]
        this.board[cell.xPos][cell.yPos] = newTile
    }

    fun move(direction: String) {
        val vector: Pair<Int, Int> = Pair(-1, 0)

        val degrees = when (direction) {
            "R" -> 270
            "L" -> 90
            "U" -> 0
            "D" -> 180
            else -> 0
        }

        this.board = rotateMatrix(this.board, degrees)
        val cells = notEmptyCell().toMutableList()

        for (cell in cells) {
            var newX = cell.xPos
            var newY = cell.yPos

            while (true) {
                val nextX = newX + vector.first
                val nextY = newY + vector.second

                if (nextX !in 0 until size || nextY !in 0 until size) {
                    // Достигнуты границы игрового поля, прерываем движение
                    break
                }

                val collisionCell = cells.find { it.xPos == nextX && it.yPos == nextY && it.value != 0 }

                if (collisionCell != null) {
                    //println("[][](${cell.xPos} ${cell.yPos} ${cell.value}) (${collisionCell.xPos} ${collisionCell.yPos} ${collisionCell.value})")
                    if (collisionCell.value == cell.value) {
                        // Объединение ячеек при столкновении
                        collisionCell.value *= 2
                        cell.value = 0
                    }
                    break // Прерываем цикл, так как достигнуто столкновение
                }

                // Перемещаем ячейку вдоль вектора
                cell.xPos = nextX
                cell.yPos = nextY

                newX = nextX
                newY = nextY
            }
        }

        this.updateBoard(cells)
        this.step++
        this.board = rotateMatrix(this.board, 360 - degrees)
    }

    private fun rotateMatrix(matrix: Array<Array<Int>>, degrees: Int): Array<Array<Int>> {
        if (degrees == 0 || degrees == 360) {
            return matrix
        }
        val n = matrix.size
        val result = Array(n) { Array(n) { 0 } }

        for (i in 0 until n) {
            for (j in 0 until n) {
                when (degrees) {
                    90 -> result[i][j] = matrix[n - j - 1][i]
                    180 -> result[i][j] = matrix[n - i - 1][n - j - 1]
                    270 -> result[i][j] = matrix[j][n - i - 1]
                    else -> throw NoSuchFieldError("Can't rotate matrix on $degrees degrees")
                }
            }
        }
        return result
    }

    private fun updateBoard(cells: List<Cell>) {
        this.board = Array(size) { Array(size) { 0 } }
        for (cell in cells.filter { it.value != 0 }) {
            board[cell.xPos][cell.yPos] = cell.value
        }
        newTiles()
    }

    init {
        this.randomSeeds = RandomSeeds(Random.nextInt(30, 50), Random.nextInt(5,10), Random.nextInt(80,120))
        newTiles()
        newTiles()
    }

    class RandomSeeds(var seed:Int= 1 , var multiplier:Int=1, var modules:Int=1)
    private class Cell(var xPos: Int, var yPos: Int, var value: Int = 0)
}