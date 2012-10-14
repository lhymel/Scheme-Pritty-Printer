// Scanner.java -- the implementation of class Scanner

import java.io.*;

class Scanner {
  private PushbackInputStream in;
  private byte[] buf = new byte[1000];

  public Scanner(InputStream i) { in = new PushbackInputStream(i); }
    
  public Token getNextToken() {
    int bite = -1;
	
    // It would be more efficient if we'd maintain our own input buffer
    // and read characters out of that buffer, but reading individual
    // characters from the input stream is easier.
    try {
      bite = in.read();
    } catch (IOException e) {
      System.err.println("We fail: " + e.getMessage());
    }

    // skip white space
    if(bite == 32) // ascii(32) = ' ' 
    	return null;
    
    // skip all comments
    if(bite == 59) { // ascii(50) = ';' 
    	// skip all bites after the simicolon until a new line is found
    	// This feature will help if we decide to read from a file
    	// instead of the command line
    	do {
    		try {
    			bite = in.read();
    		} catch (IOException e) {
    			System.err.println("Skip comment fail due stream close: " + e.getMessage());
    		}
    	} while (bite != 10) ; // ascii(10) = NL line feed, new line 
    	return null;
    }
    
    // End of Stream
    if (bite == -1)
      return null;
    
    char ch = (char) bite;
	
    // Special characters
    if (ch == '\'')
      return new Token(Token.QUOTE);
    else if (ch == '(')
      return new Token(Token.LPAREN);
    else if (ch == ')')
      return new Token(Token.RPAREN);
    else if (ch == '.')
      // We ignore the special identifier `...'.
      return new Token(Token.DOT);

    // Boolean constants
    else if (ch == '#') {
      try {
	bite = in.read();
      } catch (IOException e) {
	System.err.println("We fail: " + e.getMessage());
      }

      if (bite == -1) {
	System.err.println("Unexpected EOF following #");
	return null;
      }
      ch = (char) bite;
      if (ch == 't')
	return new Token(Token.TRUE);
      else if (ch == 'f')
	return new Token(Token.FALSE);
      else {
	System.err.println("Illegal character '" + (char) ch + "' following #");
	return getNextToken();
      }
    }

    // String constants
    else if (ch == '"') {
      // scan a string into the buffer variable buf
    	buf[0] = (byte) ch;
    	System.out.println(ch);
    	int i = 1;
    	do {
    		try {
    			bite = in.read();
    			buf[i] = (byte) bite;
    			ch = (char) bite;
    			System.out.println(ch);
    		} catch (IOException e) {
  			  System.err.println("We fail: " + e.getMessage());
    		}
    		i++;
    	} while(ch != '"');

      return new StrToken(buf.toString());
    }

    // Integer constants
    else if (ch >= '0' && ch <= '9') {
      int i = ch - '0';
      // TODO: scan the number and convert it to an integer

      // put the character after the integer back into the input
      // in->putback(ch);
      return new IntToken(i);
    }

    // Identifiers
    else if (ch >= 'A' && ch <= 'Z'
	     /* or ch is some other valid first character for an identifier */) {
      // TODO: scan an identifier into the buffer

      // put the character after the identifier back into the input
      // in->putback(ch);
      return new IdentToken(buf.toString());
    }

    // Illegal character
    else {
      System.err.println("Illegal input character '" + (char) ch + '\'');
      return getNextToken();
    }
  };
}
