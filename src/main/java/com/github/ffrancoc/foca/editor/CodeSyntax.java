package com.github.ffrancoc.foca.editor;

import com.github.ffrancoc.foca.MainApplication;
import javafx.application.Platform;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import org.fxmisc.richtext.CodeArea;
import org.fxmisc.richtext.GenericStyledArea;
import org.fxmisc.richtext.model.Paragraph;
import org.fxmisc.richtext.model.StyleSpans;
import org.fxmisc.richtext.model.StyleSpansBuilder;
import org.reactfx.collection.ListModification;

import java.util.Collection;
import java.util.Collections;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CodeSyntax {
    private Pattern PATTERN;
    private CodeArea codeArea;

    // Palabras reservadas de MariaDB
    private final String[] KEYWORDS = new String[] {
            "accesible", "add", "all", "alter", "analyze", "and", "as", "asc",
            "asensitive", "before", "between", "bigint", "binary", "blob", "both",
            "by", "call", "cascade", "case", "change", "char", "character", "check", "collate",
            "column", "condition", "constraint", "continue", "convert", "create", "cross", "current_date",
            "current_rol", "current_time", "current_timestamp", "current_user", "cursor", "database", "databases",
            "day_hour", "day_microsecond", "day_minute", "day_second", "dec", "decimal", "declare", "default",
            "delayed", "delete", "desc", "describe", "deterministic", "distinct", "distinctrow", "div", "do_domains_id",
            "double", "drop", "dual", "each", "else", "elsif", "enclosed", "escaped", "except", "exists", "exit", "explain",
            "false", "fetch", "float", "float4", "float8", "for", "force", "foreign", "from", "fulltext", "general", "grant",
            "group", "having", "high_priority", "hour_microsecond", "hour_minute", "hour_microsecond", "if", "ignore", "ignore_domain_ids",
            "ignore_server_ids", "in", "index", "infile", "inner", "inout", "insensitive", "insert", "int", "int1", "int2", "int3", "int4", "int8",
            "integer", "intersect", "interval", "into", "is", "iterate", "joing", "key", "keys", "kill", "leading", "leave", "left", "like", "limit",
            "linear", "lines", "load", "localtime", "localtimestamp", "lock", "long", "longblob", "longtext", "loop", "low_priority", "master_heartbeat_period",
            "master_ssl_verify_server_cert", "match", "maxvalue", "mediumblob", "mediumint", "mediumtext", "middleint", "minute_microsecond",
            "minute_second", "mod", "modifies", "natural", "not", "not_write_to_binlog", "null", "numeric", "offset", "on", "optimize", "option",
            "optionally", "or", "order", "out", "outer", "outfile", "over", "page_checksum", "parse_vcol_expr", "partition", "position",
            "precision", "primary", "procedure", "purge", "range", "read", "reads", "read_write", "real", "recursive", "ref_system_id", "references",
            "regexp", "release", "rename", "repeat", "replace", "require", "resignal", "restrict", "return", "returning", "revoke", "right", "rlike",
            "rows", "schema", "schemas", "second_microsecond", "select", "sensitive", "separator", "set", "show", "signal", "slow", "smallint",
            "spatial", "specific", "sql", "sqlexception", "sqlstate", "sqlwarning", "sql_big_result", "sql_calc_found_rows", "sql_small_result",
            "ssl", "starting", "stats_auto_recalc", "stats_persistent", "stats_sample_pages", "straight_join", "table", "terminated", "then", "tinyblob",
            "tinyint", "tinytext", "to", "trailing", "trigger", "true", "undo", "union", "unique", "unlock", "unsigned", "update", "usage", "use", "using",
            "utc_date", "utc_time", "utc_timestamp", "values", "varbinary", "varchar", "varcharacter", "varying", "when", "where", "while", "window", "with",
            "write", "xor", "year_month", "zerofill"
    };

    private String[] TABLENAMES = new String[] {};
    private String[] COLUMN_NAMES = new String[] {};

    public void setTABLENAMES(String[] TABLENAMES) {
        this.TABLENAMES = TABLENAMES;
        init();
    }

    public void setCOLUMN_NAMES(String[] COLUMN_NAMES) {
        this.COLUMN_NAMES = COLUMN_NAMES;
        init();
    }

    // Constructor incial de la clase
    public CodeSyntax(CodeArea codeArea) {
        this.codeArea = codeArea;
        init();
    }

    private void init() {
        // Creacion de los patrones de sintaxis
        String KEYWORD_PATTERN = "(?i)\\b(" + String.join("|", KEYWORDS) + ")\\b";
        String TABLENAME_PATTERN = "\\b(" + String.join("|", TABLENAMES) + ")\\b" + "|" + "\\`(" + String.join("|", TABLENAMES) + ")\\`";
        String COLUMN_NAME_PATTERN = "\\b(" + String.join("|", COLUMN_NAMES) + ")\\b";
        String PAREN_PATTERN = "\\(|\\)";
        //private static final String BRACE_PATTERN = "\\{|\\}";
        String BRACKET_PATTERN = "\\[|\\]";
        String SEMICOLON_PATTERN = "\\;";
        String STRONG_ACCENT_PATTERN = "\\`([^`\\\\\\\\]|\\\\\\\\.)*\\`";
        String STRING_SINGLE_QUOTE_PATTERN = "\'([^'\\\\]|\\\\.)*\'";
        String STRING_DOUBLE_QUOTE_PATTERN = "\"([^\"\\\\]|\\\\.)*\"";
        String NUMBER_PATTERN = "-?[1-9]\\d*|0";
        String COMMENT_PATTERN = "#[^\n]*" + "|" + "--[^\n]*" + "|" + "/\\*[^\\v]*" + "|" + "^\\h*\\*([^\\v]*|/)" + "|"
                + "/\\*(.|\\R)*?\\*/";


        // Compilando los patrones de sintaxis
        PATTERN = Pattern.compile(
                "(?<KEYWORD>" + KEYWORD_PATTERN + ")"
                        + "|(?<TABLENAME>" + TABLENAME_PATTERN + ")"
                        + "|(?<COLUMNNAME>" + COLUMN_NAME_PATTERN + ")"
                        + "|(?<PAREN>" + PAREN_PATTERN + ")"
                        + "|(?<BRACKET>" + BRACKET_PATTERN + ")"
                        + "|(?<SEMICOLON>" + SEMICOLON_PATTERN + ")"
                        + "|(?<STRONGACCENT>" + STRONG_ACCENT_PATTERN + ")"
                        + "|(?<STRINGSINGLEQUOTE>" + STRING_SINGLE_QUOTE_PATTERN + ")"
                        + "|(?<STRINGDOUBLEQUOTE>" + STRING_DOUBLE_QUOTE_PATTERN + ")"
                        + "|(?<NUMBER>" + NUMBER_PATTERN + ")"
                        + "|(?<COMMENT>" + COMMENT_PATTERN + ")"
                //Pattern.CASE_INSENSITIVE
        );

        codeArea.getVisibleParagraphs().addModificationObserver
                (
                        new VisibleParagraphStyler<>( codeArea, this::computeHighlighting )
                );

        // Sangría automática: inserta las sangrías de la línea anterior al entrar
        final Pattern whiteSpace = Pattern.compile( "^\\s+" );
        codeArea.addEventHandler( KeyEvent.KEY_PRESSED, KE ->
        {
            if ( KE.getCode() == KeyCode.ENTER ) {
                int caretPosition = codeArea.getCaretPosition();
                int currentParagraph = codeArea.getCurrentParagraph();
                Matcher m0 = whiteSpace.matcher( codeArea.getParagraph( currentParagraph).getSegments().get( 0 ) );
                if ( m0.find() ) Platform.runLater( () -> codeArea.insertText( caretPosition, m0.group() ) );
            }
        });

        // Agregando la hoja de estilos para el rsaltado de sintaxis
        codeArea.getStylesheets().add(MainApplication.class.getResource("css/sql-keyword.css").toExternalForm());
    }


    // Funcion que devuelve una coleccion con las palabras encontradas
    private StyleSpans<Collection<String>> computeHighlighting(String text) {
        Matcher matcher = PATTERN.matcher(text);
        int lastKwEnd = 0;
        StyleSpansBuilder<Collection<String>> spansBuilder
                = new StyleSpansBuilder<>();
        while(matcher.find()) {
            String styleClass =
                    matcher.group("KEYWORD") != null ? "keyword" :
                            matcher.group("TABLENAME") != null ? "tablename" :
                                    matcher.group("COLUMNNAME") != null ? "columnname" :
                                        matcher.group("PAREN") != null ? "paren" :
                                    //matcher.group("BRACE") != null ? "brace" :
                                            matcher.group("BRACKET") != null ? "bracket" :
                                                    matcher.group("SEMICOLON") != null ? "semicolon" :
                                                            matcher.group("STRONGACCENT") != null ? "strongaccent" :
                                                                matcher.group("STRINGSINGLEQUOTE") != null ? "string-single-quote" :
                                                                    matcher.group("STRINGDOUBLEQUOTE") != null ? "string-double-quote" :
                                                                            matcher.group("NUMBER") != null ? "number" :
                                                                                matcher.group("COMMENT") != null ? "comment" :
                                                                                    null; assert styleClass != null;
            spansBuilder.add(Collections.emptyList(), matcher.start() - lastKwEnd);
            spansBuilder.add(Collections.singleton(styleClass), matcher.end() - matcher.start());
            lastKwEnd = matcher.end();
        }
        spansBuilder.add(Collections.emptyList(), text.length() - lastKwEnd);
        return spansBuilder.create();
    }

    // Funcion para agrgar sintaxis a las palabras
    private class VisibleParagraphStyler<PS, SEG, S> implements Consumer<ListModification<? extends Paragraph<PS, SEG, S>>>
    {
        private final GenericStyledArea<PS, SEG, S> area;
        private final Function<String,StyleSpans<S>> computeStyles;
        private int prevParagraph, prevTextLength;

        public VisibleParagraphStyler(GenericStyledArea<PS, SEG, S> area, Function<String, StyleSpans<S>> computeStyles )
        {
            this.computeStyles = computeStyles;
            this.area = area;
        }

        @Override
        public void accept( ListModification<? extends Paragraph<PS, SEG, S>> lm )
        {
            if ( lm.getAddedSize() > 0 )
            {
                int paragraph = Math.min( area.firstVisibleParToAllParIndex() + lm.getFrom(), area.getParagraphs().size()-1 );
                String text = area.getText( paragraph, 0, paragraph, area.getParagraphLength( paragraph ) );

                if ( paragraph != prevParagraph || text.length() != prevTextLength )
                {
                    int startPos = area.getAbsolutePosition( paragraph, 0 );
                    Platform.runLater( () -> area.setStyleSpans( startPos, computeStyles.apply( text ) ) );
                    prevTextLength = text.length();
                    prevParagraph = paragraph;
                }
            }
        }
    }
}
