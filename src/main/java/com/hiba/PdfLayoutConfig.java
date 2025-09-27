package com.hiba;

import org.apache.pdfbox.pdmodel.common.PDRectangle;

public class PdfLayoutConfig {

    // Marges et positions
    public static final float MARGIN = 25;
    public static final float START_Y = 800;
    public static final float LINE_HEIGHT = 12;
    public static final float BOTTOM_MARGIN = 25;
    public static final float INDENT_SIZE = 10; //clé/VALEUR

    public static final float MAX_WIDTH = PDRectangle.A4.getWidth() - 2 * MARGIN;
    // Indentation pour les titres
    public static final float SECTION_INDENT = 0f;
    public static final float SUBSECTION_INDENT = 10f;
    public static final float SUBSUBSECTION_INDENT = 35f;


    // Tailles des polices
    public static final int TITLE_SIZE = 14;         // Titre du PDF
    public static final int SECTION_SIZE = 10;       // Section
    public static final int SUBSECTION_SIZE = 9;     // SubSection
    public static final int SUBSUBSECTION_SIZE = 8;  // SubSubSection
    public static final int TEXT_SIZE_NORMAL = 9;

    // Espacements
    public static final float SECTION_SPACING = 0; // Espace entre titre et ligne de section
    // Espacement vertical entre deux sections (fin section → début section suivante)
    public static final float BETWEEN_SECTIONS_SPACING = 3f;

    public static final float SECTION_LINE_WIDTH = 200;




}
