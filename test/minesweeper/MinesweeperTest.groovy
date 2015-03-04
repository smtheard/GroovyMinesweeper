package minesweeper

import spock.lang.*

class MinesweeperTest extends Specification {
    Minesweeper minesweeper
    def triedToExposeCells
    def exposedCells
    def exposeNeighborsCalled

    def setup(){
        triedToExposeCells = []
        exposedCells = false
        exposeNeighborsCalled = false
        minesweeper = new Minesweeper()
    }

    def "exposeAt exposes cell at a valid position" (){
        minesweeper.exposeAt(2, 1)
        expect:
        minesweeper.statusOfCell(2, 1) == Minesweeper.CellStatus.EXPOSED
    }

    def "exposeAt does not modify a cell that is already exposed"(){

        minesweeper.exposeAt(4, 2)
        minesweeper.exposeAt(4, 2)
        expect:
        minesweeper.statusOfCell(4, 2) == Minesweeper.CellStatus.EXPOSED
    }

    def "exposeAt throws ArrayIndexOutOfBoundsException if the row or column is out of bounds" (){
        when:
        minesweeper.exposeAt(row, column)

        then:
        thrown ArrayIndexOutOfBoundsException

        where:
        row | column
        -1  | 0
        10  | 0
        0   | -1
        0   | 10
    }

    def mockOutTryToExpose(instance) {
      instance.metaClass.tryToExpose = {row, column ->
          triedToExposeCells << [row, column]
      }      
    }
    
    def "exposeAt will try to expose all of its neighbors" (){
        mockOutTryToExpose(minesweeper)
        
        minesweeper.exposeAt(5, 5)

        expect:
        triedToExposeCells == [[4, 4], [4, 5], [4, 6], [5, 4], [5, 6], [6, 4], [6, 5], [6, 6]]
    }

    def "exposeAt will not try to expose the initial position again" () {
        mockOutTryToExpose(minesweeper)

        minesweeper.exposeAt(5, 5)
        expect:
        !triedToExposeCells.contains([5, 5])
    }

    def "exposeAt will not execute tryToExpose if the cell passed to exposeAt has already been exposed" () {
        mockOutTryToExpose(minesweeper)

        minesweeper.exposeAt(5, 5)
        triedToExposeCells.clear()
        minesweeper.exposeAt(5, 5)
        expect:
        triedToExposeCells == []
    }

    def "tryToExpose will call exposeAt for a valid cell" (){
        minesweeper.tryToExpose(5, 6)

        expect:
        minesweeper.statusOfCell(5, 6) == Minesweeper.CellStatus.EXPOSED
    }

    def mockOutExposeAt(instance) {
        instance.metaClass.exposeAt = {row, column ->
            exposedCells = true
        }
    }

    def "tryToExpose will not call exposeAt if the row or column is out of bound"(){
        mockOutExposeAt(minesweeper)
        when:
        minesweeper.tryToExpose(row, column)
        then:
        exposedCells == false
        where:
        row | column
        -1  | 0
        10  | 0
        0   | -1
        0   | 10
    }

    def "sealAt will seal a cell at a valid position"(){
        minesweeper.sealAt(4, 4)

        expect:
        minesweeper.statusOfCell(4, 4) ==  Minesweeper.CellStatus.SEALED
    }

    def "sealAt throws ArrayIndexOutOfBoundsException if the row or column is out of bounds"(){
        when:
        minesweeper.sealAt(row, column)
        then:
        thrown ArrayIndexOutOfBoundsException
        where:
        row | column
        -1  | 0
        10  | 0
        0   | -1
        0   | 10
    }

    def "sealAt does not seal an exposed cell"(){
        minesweeper.exposeAt(5, 5)
        minesweeper.sealAt(5, 5)
        expect:
        minesweeper.statusOfCell(5, 5) != Minesweeper.CellStatus.SEALED
    }

    def "unsealAt will unseal a cell that is currently sealed"(){
        minesweeper.sealAt(4, 4)
        minesweeper.unsealAt(4, 4)
        expect:
        minesweeper.statusOfCell(4, 4) != Minesweeper.CellStatus.SEALED
    }

    def "unsealAt will not unseal an exposed cell"(){
        minesweeper.exposeAt(4, 2)
        minesweeper.unsealAt(4, 2)
        expect:
        minesweeper.statusOfCell(4, 2) == Minesweeper.CellStatus.EXPOSED
    }

    def "exposing a sealed cell does not propagate a call to exposeNeighbors"(){
        mockOutTryToExpose(minesweeper)

        minesweeper.sealAt(6, 7)
        minesweeper.exposeAt(6, 7)
        expect:
        triedToExposeCells == []
    }

    def "isMine returns true if there is a mine at the given row, column"(){
        minesweeper.mineField << [5, 5]
        expect:
        minesweeper.isMine(5, 5)
    }

    def "isMine returns false if there is no mine at the given row, column"(){
        expect:
        !minesweeper.isMine(5, 5)
    }

    def "countAdjacentMines returns 2 if there are 2 mines adjacent to a given cell"(){
        minesweeper.mineField << [8, 9]
        minesweeper.mineField << [9, 8]

        expect:
        minesweeper.countAdjacentMines(9, 9) == 2
    }

    def "isAdjacent returns true if the cell has mines around it"(){
        minesweeper.mineField << [4, 4]
        expect:
        minesweeper.isAdjacent(4, 3)
    }

    def "isAdjacent returns false if the cell is a mine"(){
        minesweeper.mineField << [6, 6]
        expect:
        !minesweeper.isAdjacent(6, 6)
    }

    def "isAdjacent returns false if there are no mines around it"(){
        minesweeper.mineField << [7, 7]
        expect:
        !minesweeper.isAdjacent(2, 3)
    }

    def "isEmpty returns true on an empty cell"(){

        expect:
        minesweeper.isEmpty(6, 6)
    }

    def "isEmpty returns false if the cell is adjacent"(){

        minesweeper.mineField << [5, 5]
        expect:
        !minesweeper.isEmpty(4, 5)
    }

    def "isEmpty returns false if the cell is a mine"(){

        minesweeper.mineField << [5, 5]
        expect:
        !minesweeper.isEmpty(5, 5)
    }

    def mockOutExposeNeighbors(instance){
        instance.metaClass.exposeNeighbors = {row, column ->
            exposeNeighborsCalled = true
        }
    }

    def "exposing an adjacent cell does not propagate a call to exposeNeighbors"(){
        mockOutExposeNeighbors(minesweeper)
        minesweeper.mineField << [5, 5]
        minesweeper.exposeAt(5, 4)
        expect:
        exposeNeighborsCalled == false
    }

    def "exposing a mine cell does not propagate a call to exposeNeighbors"(){
        mockOutExposeNeighbors(minesweeper)
        minesweeper.mineField << [5, 5]
        minesweeper.exposeAt(5, 5)
        expect:
        exposeNeighborsCalled == false
    }

    def "exposing a mine causes gameStatus to return LOST"(){
        minesweeper.mineField << [5, 5]
        minesweeper.exposeAt(5, 5)
        expect:
        minesweeper.gameStatus() == Minesweeper.GameStatus.LOST
    }

    def "exposing all non-mine cells causes gameStatus to return WON"(){
        minesweeper.mineField << [5, 5]
        minesweeper.exposeAt(3, 3)
        expect:
        minesweeper.gameStatus() == Minesweeper.GameStatus.WON
    }

    def "doing nothing causes gameStatus to return INPROGRESS"(){
        expect:
        minesweeper.gameStatus() == Minesweeper.GameStatus.INPROGRESS
    }

    def "generateRandomMineField generates exactly 10 mines"(){
        minesweeper.generateRandomMineField()
        expect:
        minesweeper.mineField.size() == 10
    }

    def "generateRandomMineField pseudo randomly distributes mines to the mineField"(){
        minesweeper.generateRandomMineField()
        def minesweeper2 = new Minesweeper()
        minesweeper2.generateRandomMineField()
        expect:
        !(minesweeper.mineField == minesweeper2.mineField)
    }
}