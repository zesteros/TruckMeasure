package com.mx.vise.zebraprinterandroid.entities;

import android.graphics.Bitmap;

import com.mx.vise.zebraprinterandroid.exceptions.TicketIndexOutOfBoundsException;


/**
 * **************************VISE*******************************
 * *******************DEPARTAMENTO DE T.I.**********************
 * <p>
 * Creado por aloza el sábado 13 de octubre del 2018
 *
 * @author Angelo de Jesus Loza Martinez
 * @version acarreosandroid
 */
public class PrintObject {
    private PrintType printType;
    private String title;
    private String content;
    private boolean doubleLine;
    private int x;
    private int y;
    private int xSpace = 175;
    private int doubleLineX;
    private int lineLengthX;
    private static final int X_LIMIT_TICKET = 350;
    private Bitmap image;
    private int codeBarHeight;
    private CodebarOrientation codebarOrientation;
    private int lineLengthY;
    private int textHeight = 25;
    private int textWidth = 25;
    public PrintType getPrintType() {
        return printType;
    }

    public PrintObject setPrintType(PrintType printType) {
        this.printType = printType;
        return this;
    }

    public String getTitle() {
        return title;
    }

    public PrintObject setTitle(String title) {
        this.title = title;
        return this;
    }

    public String getContent() {
        return content;
    }

    public PrintObject setContent(String content) {
        this.content = content;
        return this;
    }

    public boolean isDoubleLine() {
        return doubleLine;
    }

    public PrintObject isDoubleLine(boolean doubleLine) {
        this.doubleLine = doubleLine;
        return this;
    }

    public int getX() {
        return x;
    }

    public PrintObject setX(int x) {
        this.x = x;
        return this;
    }

    public int getY() {
        return y;
    }

    public PrintObject setY(int y) {
        this.y = y;
        return this;
    }

    public int getxSpace() {
        return xSpace;
    }

    public PrintObject setxSpace(int xSpace) {
        this.xSpace = xSpace;
        return this;
    }

    public int getLineLengthX() {
        return lineLengthX;
    }
    public PrintObject setLineLengthX(int lineLengthX) throws TicketIndexOutOfBoundsException {
        if(lineLengthX > X_LIMIT_TICKET) throw new TicketIndexOutOfBoundsException("Largo de linea sobrepasa los limites del ticket");
        this.lineLengthX = lineLengthX;
        return this;
    }

    public Bitmap getImage() {
        return image;
    }

    public PrintObject setImage(Bitmap image) {
        this.image = image;
        return this;
    }

    public int getDoubleLineX() {
        return doubleLineX;
    }

    public PrintObject setDoubleLineX(int doubleLineX) {
        this.doubleLineX = doubleLineX;
        return this;
    }

    public int getCodeBarHeight() {
        return codeBarHeight;
    }

    public PrintObject setCodeBarHeight(int codeBarHeight) {
        this.codeBarHeight = codeBarHeight;
        return this;
    }

    public CodebarOrientation getCodebarOrientation() {
        return codebarOrientation;
    }

    public String getCodebarOrientationZpl() {
        return getCodebarOrientation() == CodebarOrientation.HORIZONTAL ? "N" : "R";
    }

    public PrintObject setCodebarOrientation(CodebarOrientation codebarOrientation) {
        this.codebarOrientation = codebarOrientation;
        return this;
    }

    public int getLineLengthY() {
        return lineLengthY;
    }

    public PrintObject setLineLengthY(int lineLengthY) {
        this.lineLengthY = lineLengthY;
        return this;
    }

    public int getTextHeight() {
        return textHeight;
    }

    public PrintObject setTextHeight(int textHeight) {
        this.textHeight = textHeight;
        return this;
    }

    public int getTextWidth() {
        return textWidth;
    }

    public PrintObject setTextWidth(int textWidth) {
        this.textWidth = textWidth;
        return this;
    }
}
