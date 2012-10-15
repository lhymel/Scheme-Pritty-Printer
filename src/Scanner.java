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
      return new Token(TokenType.QUOTE);
    else if (ch == '(')
      return new Token(TokenType.LPAREN);
    else if (ch == ')')
      return new Token(TokenType.RPAREN);
    else if (ch == '.')
      // We ignore the special identifier `...'.
      return new Token(TokenType.DOT);

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
	return new Token(TokenType.TRUE);
      else if (ch == 'f')
	return new Token(TokenType.FALSE);
      else {
	System.err.println("Illegal character '" + ch + "' following #");
	return getNextToken();
      }
    }

    // String constants
    else if (ch == '"') {
      // scan a string into the buffer variable buf
    	buf[0] = (byte) ch;
    	int i = 1;
    	do {
    		try {
    			bite = in.read();
    			buf[i] = (byte) bite;
    			ch = (char) bite;
        		i++;
    		} catch (IOException e) {
  			  System.err.println("We fail: " + e.getMessage());
    		}
    	} while(ch != '"');

      return new StrToken(new String(buf));
    }

    // Integer constants
    else if (ch >= '0' && ch <= '9') {
      int i = ch - '0';
      int result = 0;
      
      do {
    	  try {
	    	  result = result * 10 + i;
	    	  ch = (char) in.read();
	    	  i = ch - '0';
    	  } catch (IOException e) {
    		  System.out.println("Failed to get Integer constants: " + e.getMessage());
    	  }
      } while (ch >= '0' && ch <= '9');

      // put the character after the integer back into the input
      // in->putback(ch);
      try {
          in.unread((byte) ch);
      } catch (IOException e) {
    	  System.err.println("We Fail: " + e.getMessage());
      }

      return new IntToken(result);
    }

    // Identifiers
    else if (ch >= 'A' && ch <= 'Z' || ch >= 'a' && ch <= 'z') {
    	buf[0] = (byte) ch;
    	int i = 1;
    	do {
    		try {
    			bite = in.read();
    			buf[i] = (byte) bite;
    			ch = (char) bite;
        		i++;
    		} catch (IOException e) {
  			  System.err.println("We fail: " + e.getMessage());
    		}
    	} while(ch >= 'A' && ch <= 'Z' || ch >= 'a' && ch <= 'z');

      // put the character after the identifier back into the input
      // in->putback(ch);
    	try {
            in.unread((byte) ch);
        } catch (IOException e) {
      	  System.err.println("We Fail: " + e.getMessage());
        }
      return new IdentToken(new String(buf));
    }

    // Illegal character
    else {
      System.err.println("Illegal input character '" + ch + '\'');
      return getNextToken();
    }
  };
}
