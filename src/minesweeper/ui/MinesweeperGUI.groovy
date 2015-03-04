package minesweeper.ui

import minesweeper.Minesweeper

import javax.swing.*
import java.awt.GridLayout
import java.awt.event.MouseEvent
import java.awt.event.MouseListener


class MinesweeperFrame extends JFrame {
    def gameBoard
    def minesweeper

    void frameInit() {
        minesweeper = new Minesweeper()
        minesweeper.generateRandomMineField()
        super.frameInit()
        this.setLayout(new GridLayout(10, 10))
        gameBoard = new Object[10][10]
        for (row in 0..9)
            for (column in 0..9) {
                gameBoard[row][column] = new JButton()
                gameBoard[row][column].addMouseListener(new ActionHandler())
                this.add(gameBoard[row][column])
            }
    }


    class ActionHandler implements MouseListener {
        def clickedCellRow, clickedCellCol
        void mouseEntered(MouseEvent e){}
        void mouseExited(MouseEvent e){}
        void mouseClicked(MouseEvent e){}
        void mousePressed(MouseEvent e){}

        void mouseReleased(MouseEvent e){

            for (row in 0..9)
                for (column in 0..9) {
                    if (gameBoard[row][column] == e.getSource()) {
                        clickedCellRow = row
                        clickedCellCol = column
                    }
                }
            
            if (SwingUtilities.isLeftMouseButton(e) && !e.isControlDown()) {
                minesweeper.exposeAt(clickedCellRow, clickedCellCol)
                cascadeExpose()
                displayAdjacentNum()
                if(minesweeper.gameStatus() == Minesweeper.GameStatus.LOST) {
                    JOptionPane.showMessageDialog(new JFrame(), "You Lose, sorry!")
                    System.exit(0)
                }
                else if(minesweeper.gameStatus() == Minesweeper.GameStatus.WON) {
                    JOptionPane.showMessageDialog(new JFrame(), "You Win!")
                    System.exit(0)
                }
            }

            if(SwingUtilities.isRightMouseButton(e) || e.isControlDown()){
                if(minesweeper.statusOfCell(clickedCellRow, clickedCellCol) == Minesweeper.CellStatus.UNEXPOSED){
                    minesweeper.sealAt(clickedCellRow, clickedCellCol)
                    gameBoard[clickedCellRow][clickedCellCol].setText("S")
                }
                else if(minesweeper.statusOfCell(clickedCellRow, clickedCellCol) == Minesweeper.CellStatus.SEALED){
                    minesweeper.unsealAt(clickedCellRow, clickedCellCol)
                    gameBoard[clickedCellRow][clickedCellCol].setText("")
                }
            }
        }

        def cascadeExpose(){
            for (row in 0..9)
                for (column in 0..9) {
                    if(minesweeper.statusOfCell(row, column) == Minesweeper.CellStatus.EXPOSED)
                        gameBoard[row][column].setEnabled(false)
                }
        }

        def displayAdjacentNum(){
            for (row in 0..9)
                for (column in 0..9) {
                    if(minesweeper.statusOfCell(row, column) == Minesweeper.CellStatus.EXPOSED && minesweeper.isAdjacent(row, column) && !minesweeper.isMine(row, column))
                        gameBoard[row][column].setText(minesweeper.countAdjacentMines(row, column).toString())
                    if(minesweeper.statusOfCell(row, column) == Minesweeper.CellStatus.EXPOSED && minesweeper.isMine(row, column)) {
                        gameBoard[row][column].setText("M")
                    }
                }
        }
    }
}

MinesweeperFrame frame = new MinesweeperFrame()
frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE)
frame.setVisible(true)
frame.setSize(500, 500)





