package minesweeper


class Minesweeper {
    enum CellStatus{UNEXPOSED, EXPOSED, SEALED}
    enum GameStatus{INPROGRESS, WON, LOST}
    def cellStatus
    def mineField

    Minesweeper(){
        cellStatus = new Object[10][10]
        mineField = []
        for(row in 0..9)
            for(column in 0..9)
                cellStatus[row][column] = CellStatus.UNEXPOSED
    }

    def exposeAt(row, column) {
        verifyBounds(row, column)

        if(statusOfCell(row, column) == CellStatus.UNEXPOSED) {
            cellStatus[row][column] = CellStatus.EXPOSED
            if(!isAdjacent(row, column) && !isMine(row, column))
                exposeNeighbors(row, column)
        }
    }

    def exposeNeighbors(row, column){
        tryToExpose(row - 1, column - 1)
        tryToExpose(row - 1, column)
        tryToExpose(row - 1, column + 1)

        tryToExpose(row, column - 1)
        tryToExpose(row, column + 1)

        tryToExpose(row + 1, column - 1)
        tryToExpose(row + 1, column)
        tryToExpose(row + 1, column + 1)
    }

    def statusOfCell(row, column){
        cellStatus[row][column]
    }

    def verifyBounds(row, column){
        if(!(row < 10 && row > -1 && column < 10 && column > -1))
            throw new ArrayIndexOutOfBoundsException()
    }

    def tryToExpose(row, column){
        if((row < 10 && row > -1 && column < 10 && column > -1))
            exposeAt(row, column)
    }

    def sealAt(row, column){
        verifyBounds(row, column)
        if(statusOfCell(row, column) != CellStatus.EXPOSED)
            cellStatus[row][column] = CellStatus.SEALED
    }

    def unsealAt(row, column){
        verifyBounds(row, column)
        if(statusOfCell(row, column) == CellStatus.SEALED){
            cellStatus[row][column] = CellStatus.UNEXPOSED
        }
    }

    def isMine(row, column){
        mineField.contains([row, column])
    }

    def countAdjacentMines(row, column){
        def count = 0
        if(isMine(row - 1, column - 1))
            count++
        if(isMine(row - 1, column))
            count++
        if(isMine(row - 1, column + 1))
            count++
        if(isMine(row, column - 1))
            count++
        if(isMine(row, column + 1))
            count++
        if(isMine(row + 1, column - 1))
            count++
        if(isMine(row + 1, column))
            count++
        if(isMine(row + 1, column + 1))
            count++
        return count
    }

    def isAdjacent(row, column){
        return countAdjacentMines(row, column) > 0
    }

    def isEmpty(row, column){
        return (countAdjacentMines(row, column) == 0 && !isMine(row, column))
    }

    def gameStatus(){
        for(row in 0..9)
            for(column in 0..9){
                if(cellStatus[row][column] == CellStatus.EXPOSED && isMine(row, column))
                    return GameStatus.LOST
            }

        for(row in 0..9)
            for(column in 0..9){
                if(cellStatus[row][column] != CellStatus.EXPOSED && !isMine(row, column))
                    return GameStatus.INPROGRESS
            }

        return GameStatus.WON
    }

    def generateRandomMineField(){
        def rand = new Random()
        int max = 9
        (0..9).each{
            mineField << [rand.nextInt(max+1), rand.nextInt(max+1)]
        }
    }

}
