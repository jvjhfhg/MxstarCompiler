package mxstar.exception;

import mxstar.ast.TokenPosition;
import org.antlr.v4.runtime.BaseErrorListener;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;

public class SyntaxErrorListener extends BaseErrorListener {
    private ErrorRecorder errorRecorder;

    public SyntaxErrorListener(ErrorRecorder errorRecorder) {
        this.errorRecorder = errorRecorder;
    }

    @Override
    public void syntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol, int line, int charPositionInLine, String msg, RecognitionException e) {
        errorRecorder.add(new TokenPosition(line, charPositionInLine), msg);
    }

    public boolean isError() {
        return errorRecorder.errorOccured();
    }
}
