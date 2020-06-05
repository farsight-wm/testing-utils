package farsight.testing.utils.jexl.legacy;

public class ExpressionProcessor {

	private static final char BACKSLASH = '\\';
	
	protected static final String ENC_COLON = "__col_";
	protected static final String ENC_HYPHEN = "__hyp_";
	protected static final String ENC_SPACE = "__spc_";

	private ExpressionProcessor() {}
	
	public static String escapedToEncoded(String expr) {
		int slashPos = expr.indexOf(BACKSLASH);
		if (slashPos == -1) {
			return expr;
		}
		
		StringBuilder sb = new StringBuilder();
		int lastSlash = -1;
		while (slashPos != -1) {
			sb.append(expr.substring(lastSlash +  1, slashPos));
			char c = expr.charAt(slashPos + 1);
			switch (c) {
			case ':':
				sb.append(ENC_COLON);
				break;
			case '-':
				sb.append(ENC_HYPHEN);
				break;
			case ' ':
				sb.append(ENC_SPACE);
				break;
			default:
				throw new ExpressionProcessingException("Invalid escaped character: " + c);
			}
			lastSlash = ++slashPos;
			slashPos = expr.indexOf(BACKSLASH, lastSlash + 1);
		}
		if (lastSlash + 1  < expr.length()) {
			sb.append(expr.substring(lastSlash + 1));
		}
		return sb.toString();
	}

	public static String decode(String expr) {
		int undPos = expr.indexOf("__");
		if (undPos == -1) {
			return expr;
		}
		// Inefficient, will replace as above
		return expr.replace(ENC_COLON, ":").replace(ENC_HYPHEN, "-").replace(ENC_SPACE, " ");
	}
}
