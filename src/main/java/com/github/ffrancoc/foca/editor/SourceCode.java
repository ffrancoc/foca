package com.github.ffrancoc.foca.editor;

import org.fxmisc.richtext.CodeArea;
import org.fxmisc.richtext.LineNumberFactory;

public class SourceCode extends CodeArea {
    private CodeSyntax codeSyntax;

    public void loadTableNames(String[] tableNames) {
        this.codeSyntax.setTABLENAMES(tableNames);
    }

    public void loadColumnNames(String[] columnNames) {
        this.codeSyntax.setCOLUMN_NAMES(columnNames);
    }

    public SourceCode() {
        super();
        this.codeSyntax = new CodeSyntax(this);
        init();
    }

    private void init() {
        setParagraphGraphicFactory(LineNumberFactory.get(this));
    }
}
