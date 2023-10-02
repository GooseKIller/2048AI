import java.lang.Exception
import kotlin.math.pow

class AiPathFinder(private var deepMove:Int = 3, private var randomSeeds: Game.RandomSeeds) {

    fun bestMoves(board: Array<Array<Int>> , step:Int): String {
        val bestMoves: Array<Move> = Array(4) {Move(0, "Error")}
        val directions = listOf("U", "D", "L", "R")
        for(i in bestMoves.indices){
            bestMoves[i] = bestMovesRecursion(directions[i], board, step)
        }
        //
        return bestMoves.maxBy { it.fitness }.moves
    }

    private fun bestMovesRecursion(direction:String, boardold: Array<Array<Int>>, step: Int, recursionLevel: Int = 0): Move {
        val board = move(direction, boardold, step)

        //println("Fitness: ${fitnessFunction(board)} ${direction}, ${recursionLevel}")
        if (recursionLevel == deepMove){
            //println(recursionLevel)
            if (board.flatten() == boardold.flatten()){
                //println("Eguals >>")
                return Move(0, direction)
            }
            return Move(this.fitnessFunction(board), direction)
        }

        val bestMoves: Array<Move> = Array(4) {Move(0, "Error")}
        val directions = listOf("U", "D", "L", "R")
        for(i in bestMoves.indices){
            bestMoves[i] = bestMovesRecursion(directions[i], board, 1+step, 1+recursionLevel)
            //println()
        }
        val newMove = bestMoves.maxBy { it.fitness }
        if (board.flatten() == boardold.flatten()){
            //println("Eguals >")
            newMove.fitness = 0
        } else {
            newMove.fitness += fitnessFunction(board) * recursionLevel + 1
        }
        newMove.moves = direction + newMove.moves
        return newMove
    }


    private fun move(direction: String, boardold: Array<Array<Int>>, step: Int):Array<Array<Int>> {
        var board = boardold
        val vector: Pair<Int, Int> = Pair(-1, 0)
        val size = board.size

        val degrees = when (direction) {
            "R" -> 270
            "L" -> 90
            "U" -> 0
            "D" -> 180
            else -> throw Exception("Cant rotate to $direction")
        }

        board = rotateMatrix(board, degrees)
        val cells = notEmptyCell(board).toMutableList()

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
        board = updateBoard(cells, board.size, step)
        return rotateMatrix(board, 360 - degrees)

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

    private fun emptyCell(board: Array<Array<Int>>): MutableList<Cell>{
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

    private fun notEmptyCell(board: Array<Array<Int>>): MutableList<Cell>{
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

    private fun updateBoard(cells: List<Cell>, size:Int, step: Int):Array<Array<Int>> {
        var board = Array(size) { Array(size) { 0 } }
        for (cell in cells.filter { it.value != 0 }) {
            board[cell.xPos][cell.yPos] = cell.value
        }
        board = newTiles(board, step)
        return board
    }

    private fun newTiles(board: Array<Array<Int>>, step: Int):Array<Array<Int>> {
        val emptyCells = this.emptyCell(board)
        if (emptyCells.size == 0) {
            return board
        }

        var newTile = 2
        if (step % 10 == 0) {
            newTile = 4
        }
        val cell: Cell = emptyCells[pseudoRandomInt(step)%emptyCells.size]
        board[cell.xPos][cell.yPos] = newTile
        return board
    }

    private fun pseudoRandomInt(number: Int): Int {
        // Простой пример на основе хеширования
        val seed = this.randomSeeds.seed//42 // Начальное значение семени
        val multiplier = this.randomSeeds.multiplier * (number % 8) // Множитель (Раньше здесь было 6)
        val modulus = this.randomSeeds.modules // Модуль (раньше 97)

        return (number * multiplier + seed) % modulus
    }

    private fun fitnessFunction(board: Array<Array<Int>>):Long {
        val flatBoard = board.flatten()
        val sumValues = flatBoard.sum()
        val maxValues = flatBoard.max()
        val spaceValues = flatBoard.filter { it == 0 }.size
        val mergePotential = this.calculateMergePotential(board)
        val cornerBonus = this.calculateCornerBonusSnake(board)
        val largeNumber:Int = flatBoard.sum() / (flatBoard.size - spaceValues)
        val lose:Int = if (!this.isLose(board)) 1 else 0

        return ((sumValues + 8*maxValues + 10*spaceValues + 2 * mergePotential + 15*cornerBonus + 15*largeNumber) * lose).toLong()

    }

    fun isLose(board: Array<Array<Int>>): Boolean {
        val size = board.size
        val cells = this.notEmptyCell(board)
        if (cells.size != size * size) {
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

    private fun calculateMergePotential(board: Array<Array<Int>>): Int {
        var mergePotential = 0
        for (i in 0 until board.size-1){
            for (j in 0 until board.size-1){
                if ((board[i][j] == board[i+1][j] || board[i][j] == board[i][j+1]) && board[i][j] != 0) {
                    mergePotential += board[i][j] * 2
                }
            }
        }
        return mergePotential
    }

    private fun calculateCornerBonus(board: Array<Array<Int>>): Int {
        val cornerValues = listOf(
            board[0][0],
            board[0][board.size - 1],
            board[board.size - 1][0]//,
            //board[board.size - 1][board.size - 1]
        )
        val maxValueInCorner = cornerValues.maxOrNull() ?: 0
        val sumOfCorners = cornerValues.sum()

        // Бонус за максимальное значение в углу и сумму всех значений в углах

        return maxValueInCorner * 2 + sumOfCorners
    }

    private fun calculateCornerBonussideDiagonal(board:Array<Array<Int>>): Int {
            val rows = board.size
            val cols = board[0].size
            val resultMatrix = Array(rows) { Array(cols) { 0 } }

            for (i in 0 until rows) {
                for (j in 0 until cols) {
                    val powerOfFour = 4.0.pow(rows - 1 - i + cols - 1 - j).toInt()
                    resultMatrix[i][j] = board[i][j] * powerOfFour
                }
            }

            return resultMatrix.sumOf { it.sum() }
    }

    private fun calculateCornerBonusSnake(board: Array<Array<Int>>): Long {
        val flattenBoard = board.flatten()
        val size = flattenBoard.size
        var answer:Long = 0
        for (i in flattenBoard.indices) {
            answer += 4.0.pow(size-1-i).toLong() * flattenBoard[i]

        }
        return answer
    }

    class Move(var fitness:Long, var moves:String)
    private class Cell(var xPos: Int, var yPos: Int, var value: Int = 0)
}