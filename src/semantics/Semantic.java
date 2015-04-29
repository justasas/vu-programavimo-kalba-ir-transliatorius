package semantics;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;


class Token
{
    private String type;
    private String tempVar;
    
    public Token(String tempVar, String type)
    {
        this.tempVar = tempVar;
    	this.type = type;
    }
    
    public String getType()
    {
    	return type;
    }
    
    public void setType(String type)
    {
        this.type = type;
    }
    
    public void setTempVar(String tempVar)
    {
        this.tempVar = tempVar;
    }
    
    public String getTempVar()
    {
        return tempVar;
    }
}

public class Semantic
{
    private String fileName;
    private File xmlFile;
    private DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
    private DocumentBuilder docBuilder;
    private static Document doc;
    private static TerminalsHolder terminalsHolder;
    public static Node element;
    private static Node lookupElement;
    private static SymbolsTable symbolsTable;
    private static IFGenerator iFGenerator;
    PrintWriter writer;
    
    public void print_tree(Node node)
    {
        NodeList nl = node.getChildNodes();
        for(int i = 0; i < nl.getLength(); i++)
        { // VatiableOrFuncCall pirma eina afteridentifier
            /*
            if (nl.item(i).getNodeName().contains("functionDefinitionList") || 
                    nl.item(i).getNodeName().contains("parameterList") ||
                    nl.item(i).getNodeName().contains("statements") ||
                    nl.item(i).getNodeName().contains("callVariableList") ||
                    nl.item(i).getNodeName().contains("functionCall") || // kitoks
                    nl.item(i).getNodeName().contains("block") || // kitoks
                    nl.item(i).getNodeName().contains("functionDefinition") || 
                    nl.item(i).getNodeName().contains("reservedStatement") ||
                    nl.item(i).getNodeName().contains("arrayVariable") ||
                    nl.item(i).getNodeName().equals("ifStatement") ||
                    nl.item(i).getNodeName().contains("variable") ||
                    nl.item(i).getNodeName().contains("reservedStatement")
                    )
                add_empty_node(nl.item(i));
            if (nl.item(i).getNodeName().contains("xpression") || nl.item(i).getNodeName().contains("term"))
            {
                reverse_expression(nl.item(i));
                //expressions_list.add(nl.item(i));
            }  */
            if (nl.item(i).getNodeName().equals("nothing"))
            {
                nl.item(i).getParentNode().removeChild(nl.item(i));
                continue;
            }
            if (nl.item(i).getNodeName().contains("xpression") || nl.item(i).getNodeName().contains("term")
                    || nl.item(i).getNodeName().contains("variableOrFuncCall")
                    || nl.item(i).getNodeName().contains("callVariableList")
                    || nl.item(i).getNodeName().contains("functionCall")
                    
                    )
            {
                reverse_expression(nl.item(i));
                //expressions_list.add(nl.item(i));
            }
            if (nl.item(i).hasAttributes())
              writer.print("<"+nl.item(i).getNodeName()+" "+ nl.item(i).getAttributes().item(0)+">");
            else 
              writer.print("<"+nl.item(i).getNodeName()+">");                
            print_tree(nl.item(i));
            writer.print("</"+nl.item(i).getNodeName()+">");
        }
    }
        
    
    public void reverse_expression(Node node)
    {
        /*Node clone = node.cloneNode(true);
        int size = node.getChildNodes().getLength();
        System.out.print("child node: ");
        for (int i = 0; i < node.getChildNodes().getLength(); i++)
        {
            System.out.print(node.getChildNodes().item(i)+",");
            clone.replaceChild(node.getChildNodes().item(i), clone.getChildNodes().item(--size));
        }
        
        System.out.print("\nchild node po apkeitimo: ");        
        for (int i = 0; i < node.getChildNodes().getLength(); i++)
        {
            System.out.print(clone.getChildNodes().item(i)+",");
        }
        node = clone;*/

        /*NodeList nl = node.getChildNodes();
        LinkedList<Node> nodes = new LinkedList<Node>();
        for( int i = 0; i < nl.getLength(); i++ ) {
          nodes.addFirst( nl.item( i ) );
        }
        for( Node node2 : nodes ) {
          node.appendChild( node.removeChild( node2 ) );
        }
        */
        
        //System.out.println("getLength:"+ node.getChildNodes().getLength());

        if (node.getChildNodes().getLength() == 3)
        {             
            Node first_child = node.getChildNodes().item(0).cloneNode(true);
            Node second_child = node.getChildNodes().item(1).cloneNode(true);
            Node third_child = node.getChildNodes().item(2).cloneNode(true);
            node.removeChild(node.getFirstChild());
            node.removeChild(node.getFirstChild());  
            node.removeChild(node.getFirstChild()); 
            node.appendChild(third_child);
            node.appendChild(second_child);
            node.appendChild(first_child);
        }
        if (node.getChildNodes().getLength() == 2)
        {
            Node first_child = node.getChildNodes().item(0).cloneNode(true);
            Node third_child = node.getChildNodes().item(1).cloneNode(true);
            node.removeChild(node.getFirstChild());
            node.removeChild(node.getFirstChild());           
            node.appendChild(third_child);
            node.appendChild(first_child);
        }      
    }
    
 /*   public void change_tree()
    {
        for (int i = 0; i < expressions_list.size(); i++)
        {
            System.out.print("\npries: ");
            for (int j = 0; j < expressions_list.get(i).getChildNodes().getLength(); j++)
            {
                System.out.print(expressions_list.get(i).getChildNodes().item(j) + ", ");
            }             
            reverse_expression(expressions_list.get(i));
            System.out.print("\npo: ");
            for (int j = 0; j < expressions_list.get(i).getChildNodes().getLength(); j++)
            {
                System.out.print(expressions_list.get(i).getChildNodes().item(j) + ", ");
            }        
        }
    }*/
    
    public Semantic(String fileName)
    {
        try
        {
            this.writer = new PrintWriter("the-file-name.xml", "UTF-8");
            this.fileName = fileName;
            xmlFile = new File(fileName);
            docBuilder = docBuilderFactory.newDocumentBuilder();
            doc = docBuilder.parse(xmlFile);
            doc.getDocumentElement().normalize();
            element = doc.getDocumentElement();
            terminalsHolder = new TerminalsHolder("lexems.txt");
            symbolsTable = new SymbolsTable();
            iFGenerator = new IFGenerator("intermediateForm.txt", "hiddenForm.txt");
            //search_for(element); // uzpildo expression lista surastais elementais
            //change_tree();
            writer.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
            print_tree(element.getParentNode());
            writer.close();

            
            xmlFile = new File("the-file-name.xml");
            docBuilder = docBuilderFactory.newDocumentBuilder();
            doc = docBuilder.parse(xmlFile);
            doc.getDocumentElement().normalize();
            element = doc.getDocumentElement();
            removeUseless(element);            
            
            try
            {
                TransformerFactory transfac = TransformerFactory.newInstance();
                Transformer trans = transfac.newTransformer();
                trans.transform(new DOMSource(doc), new StreamResult(new FileWriter("out.xml")));
            }
            catch(Exception e)
            {
                e.printStackTrace();
            }
            checkAndGenerate();

        }
        catch(Exception e)
        {
            System.out.println("There was some errors while parsing");
            e.printStackTrace();
        }
        
    }

//-------------------------------------------------------------------------------------------------

    private static boolean removeUseless(Node node)
    {        
        if (node == null)
        {
            return false;
        }
        if (terminalsHolder.isTerminal("<" + node.getNodeName() + ">") != -1) 
        {
            if ((node.getNodeName().equals("colon_delim")) ||
                (node.getNodeName().equals("comma_delim")) ||
                (node.getNodeName().equals("left_bracket_delim")) ||
                (node.getNodeName().equals("right_bracket_delim")) ||
                (node.getNodeName().equals("l_c_bracket_delim")) ||
                (node.getNodeName().equals("r_c_bracket_delim")) ||
                (node.getNodeName().equals("l_s_bracket_delim")) ||
                (node.getNodeName().equals("r_s_bracket_delim")))
            {
                return true;
            }
            return false;
        }
        boolean del = true;
        for (int i = node.getChildNodes().getLength() - 1; i >= 0; i--)
        {
            if(node.getNodeName().equals("arrayOrNothing"))
            {
                del = false;
                removeUseless(node.getFirstChild());
            }
            else if(node.getNodeName().equals("afterIdentifier"))
            {
                del = false;
                removeUseless(node.getFirstChild());
            }
            else if(node.getNodeName().equals("funcOrAssignment1"))
            {
                del = false;
                for(int j = 0; j < node.getChildNodes().getLength(); j++)
                {
                    removeUseless(node.getChildNodes().item(i));
                }
                
            }
            else if (removeUseless(node.getChildNodes().item(i)))
            {
                node.removeChild(node.getChildNodes().item(i));
            }
            else
            {
                del = false;
            }
        }
        return del;    
    }
    
//-------------------------------------------------------------------------------------------------

    private static void checkAndGenerate()
    {
        if(element.getFirstChild().getNodeName().equals("functionDefinitionList"))
        {
            checkFunctions(element.getFirstChild());
        }
        
        iFGenerator.addOpCode(IFOp.MAIN_START, "", "", "");
        
        if(element.getFirstChild().getNodeName().equals("mainFunction"))
        {      
            if(element.getFirstChild().getChildNodes().item(1) != null)
            {
                returnType = null;
                dealWithBlock(element.getFirstChild().getChildNodes().item(1).getFirstChild(), symbolsTable);
                
                if(returnType != null && !returnType.equals("void"))
                {
                    System.out.println("Semantic error: return type and function type doesnt match"); 
                }
            }
        }
        else
        {
            if(element.getChildNodes().item(1).getChildNodes().item(1) != null)
            {
                returnType = null;
                
                dealWithBlock(element.getChildNodes().item(1).getChildNodes().item(1).getFirstChild(), symbolsTable);
                if(returnType != null && !returnType.equals("void"))
                {
                    System.out.println("Semantic error: return type and function type doesnt match"); 
                }
            }
           
        }
        iFGenerator.addOpCode(IFOp.MAIN_END, "", "", "");
        iFGenerator.close();
    
    }

//-------------------------------------------------------------------------------------------------
    
    private static void checkFunctions(Node functionNode)
    {      
        traverseUntil(functionNode, "functionType");
        String functionType = lookupElement.getFirstChild().getFirstChild().getNodeName();

        traverseUntil(functionNode, "functionName");
        String functionName = lookupElement.getFirstChild().getAttributes().item(0).getNodeValue();

        SymbolsTable functionTable = new SymbolsTable();
        String params[] = null;
        
        iFGenerator.addOpCode(IFOp.PROC_START, "", "", functionName);
        
        lookupElement = null;
        traverseUntil(functionNode.getFirstChild(), "parameterList");
        if(lookupElement != null)
        {
            dealWithParams(lookupElement, functionTable);
            params = new String[functionTable.getSymbolsCount()];      
            for(int i = 0; i < functionTable.getSymbolsCount(); i++)
            {
            	Symbol symbol = functionTable.getSymbol(i);
            	params[i] = symbol.getVarType() + ":" + symbol.getType().toString();
            }
        }
        
        if(symbolsTable.isSymbol(functionName, IDtype.FUNCTION) != -1)
        {
            System.out.println("Semantic error: function " + functionName + " is already defined");
        }
        else
        {
            symbolsTable.add(functionName, functionType, IDtype.FUNCTION, "", params);
            functionTable.add(functionName, functionType, IDtype.FUNCTION, "", params);
        }
        
        lookupElement = null;
        traverseUntil(functionNode.getFirstChild(), "block");
        if(lookupElement != null)
        {
            returnType = null;
            dealWithBlock(lookupElement.getFirstChild(), functionTable);
            if(returnType == null)
            {
                if(!functionType.equals("void"))
                {
                    System.out.println("Semantic error: return not found in function - " + functionName);
                }
            }
            else
            {
                if(!returnType.equals(functionType))
                {
                    System.out.println("Semantic error: return type and function type doesnt match"); 
                }
            }
            
        }
        else if(!functionType.equals("void"))
        {
            System.out.println("Semantic error: return not found in function - " + functionName);
        }

        iFGenerator.addOpCode(IFOp.RET, "", "", "");

        //@ next function     
        functionNode = functionNode.getFirstChild();
        if(functionNode.getNextSibling() != null)
        {
            checkFunctions(functionNode.getNextSibling());
        }
    }

//-------------------------------------------------------------------------------------------------
    
    private static void dealWithParams(Node paramNode, SymbolsTable symbolsTable)
    {
        traverseUntil(paramNode, "type");
        String paramType = lookupElement.getFirstChild().getNodeName();
        String identifier = paramNode.getFirstChild().getChildNodes().item(1).getFirstChild().getAttributes().item(0).getNodeValue();
        if(paramNode.getFirstChild().getChildNodes().item(1).getChildNodes().item(1) == null)
        {
            if(symbolsTable.isSymbol(identifier, IDtype.VARIABLE) != -1 || symbolsTable.isSymbol(identifier, IDtype.ARRAY) != -1)
            {
            	System.out.println("Semantic error: variable " + identifier + " is already defined");
            }
            else
            {
                iFGenerator.addOpCode(IFOp.FPARAM, "", "", identifier);
       	    	symbolsTable.add(identifier, paramType, IDtype.VARIABLE, "", null);
       	    }
       	}
       	else
       	{   //array
       	    if(paramNode.getFirstChild().getChildNodes().item(1).getChildNodes().item(1).getFirstChild().getFirstChild() != null)
            {
                System.out.println("Semantic error: array bounds cant be specified");
            }
            else
            {
                //if(!dealWithExpression(paramNode.getFirstChild().getChildNodes().item(1).getChildNodes().item(1).getFirstChild().getFirstChild().getFirstChild(), symbolsTable).getType().equals("int"))
                //{
                    //System.out.println("Semantic error: array bounds must be integer type");
                //}
                //else
                //{
                    if(symbolsTable.isSymbol(identifier, IDtype.VARIABLE) != -1 || symbolsTable.isSymbol(identifier, IDtype.ARRAY) != -1)
                    {
                        System.out.println("Semantic error: variable " + identifier + " is already defined");
                    }
                    else
                    {
                        iFGenerator.addOpCode(IFOp.FPARAM, "*", "", identifier);
                        symbolsTable.add(identifier, paramType, IDtype.ARRAY, "", null);
                    }
                //}
                
            }
       	}
       	//@ next param
        paramNode = paramNode.getFirstChild();
        if(paramNode.getNextSibling() != null)
        {
            dealWithParams(paramNode.getNextSibling(), symbolsTable);
        }
    }

//-------------------------------------------------------------------------------------------------
    
    private static boolean found = false;
    private static boolean traverseUntil(Node node, String value)
    {        
    	found = false;
        if(node.getNodeName().equals(value))
        {
            lookupElement = node;
            found = true;
            return true;
        }
        if(!found)
        {
            for(int i = 0; i < node.getChildNodes().getLength(); i++)
            {
            	if(traverseUntil(node.getChildNodes().item(i), value))
            	{
            	    return true;
            	}
            }
        }
        return false;
    }
    
//-------------------------------------------------------------------------------------------------

    private static String returnType = null;
    private static void dealWithStatements(Node statementsNode, SymbolsTable localTable)
    {
        String statement = statementsNode.getFirstChild().getNodeName();

        if(statement.equals("simpleStatement"))
        {
            String simpleStatement = statementsNode.getFirstChild().getFirstChild().getNodeName();
            if (simpleStatement.equals("variableDeclaration"))
            {
                String variableType = statementsNode.getFirstChild().getFirstChild().getFirstChild().getFirstChild().getNodeName();
                
                String identifier = statementsNode.getFirstChild().getFirstChild().getChildNodes().item(1).getFirstChild().getAttributes().item(0).getNodeValue();
                IDtype type;
                String value = null;
                if(statementsNode.getFirstChild().getFirstChild().getChildNodes().item(1).getChildNodes().item(1) == null)
                {
                    type = IDtype.VARIABLE;
                }
                else
                {
                    type = IDtype.ARRAY;
                    Token token = null;
                    if(statementsNode.getFirstChild().getFirstChild().getChildNodes().item(1).getChildNodes().item(1).getFirstChild().getFirstChild() == null)
                    {
                        System.out.println("Semantic error: array bounds must be specified");
                    }
                    else
                    {
                        token = dealWithExpression(statementsNode.getFirstChild().getFirstChild().getChildNodes().item(1).getChildNodes().item(1).getFirstChild().getFirstChild().getFirstChild(), localTable);
                        String boundsType = token.getType();
                        value = token.getTempVar();
                        if(!boundsType.equals("int"))
                        {
                            System.out.println("Semantic error: array bounds must be integer type");
                        }
                    
                        if(statementsNode.getFirstChild().getFirstChild().getChildNodes().item(2) != null)
                        {
                            System.out.println("Semantic error: cant initialize array at definition");
                        }
                        else
                        {
                            iFGenerator.addOpCode(IFOp.STORA, identifier, variableType, token.getTempVar());
                        }
                    }
                    
                }
                
                // init or not
                if(statementsNode.getFirstChild().getFirstChild().getChildNodes().item(2) != null)
                {
                    
                    String expressionType = null;
                    Token token =  dealWithExpression(statementsNode.getFirstChild().getFirstChild().getChildNodes().item(2).getChildNodes().item(1).getFirstChild(), localTable);
                    if(isArray)
                    {
                        System.out.println("Semantic error: cannot use arrays in inicialisation");
                    }
                    value = token.getTempVar();
                    expressionType = token.getType();
                                        
                    if(expressionType != variableType)
                    {
                        System.out.println("Semantic error: types of variable " + identifier + " and expression doesnt not match");
                    }
                }
                                   
                if(localTable.isSymbol(identifier, IDtype.ARRAY) != -1 || localTable.isSymbol(identifier, IDtype.VARIABLE) != -1)
                {
                    System.out.println("Semantic error: variable " + identifier + " is already defined");
                }
                else
                {
                    localTable.add(identifier, variableType, type, value, null);
                    
                    if(type == IDtype.VARIABLE)
                    {
                        iFGenerator.addOpCode(IFOp.STORE, identifier, variableType, "");
                        if(value != null)
                        {
                            iFGenerator.addOpCode(IFOp.MOV, identifier, value, "");
                        }
                    }    
                }

            }
            else if (simpleStatement.equals("funcOrAssignment"))
            {
                if(!statementsNode.getFirstChild().getFirstChild().getChildNodes().item(1).getFirstChild().getNodeName().equals("functionCall"))
                {
                    String identifier = statementsNode.getFirstChild().getFirstChild().getFirstChild().getAttributes().item(0).getNodeValue();
                    String variableType = null;
                    if(localTable.isSymbol(identifier, IDtype.ARRAY) == -1 && localTable.isSymbol(identifier, IDtype.VARIABLE) == -1)
                    {
                        System.out.println("Semantic error: variable " + identifier + " is not defined");
                    }
                    else
                    {
                        boolean error = false;
                        IDtype type = null;
                        if(localTable.isSymbol(identifier, IDtype.ARRAY) == -1)
                        {
                            type = IDtype.VARIABLE;
                        }
                        else
                        {
                            type = IDtype.ARRAY;
                        }
                        
                        variableType = localTable.getSymbol(localTable.isSymbol(identifier, type)).getVarType();
                        String assignmentOp = null;
                        Token tempToken = null;
                        if(statementsNode.getFirstChild().getFirstChild().getChildNodes().item(1).getFirstChild().getNodeName().equals("arrayVariable"))
                        {
                            if(type != IDtype.ARRAY)
                            {
                                System.out.println("Semantic error: " + identifier + " is not an array");
                                error = true;
                            }
                            else
                            {
                                if(statementsNode.getFirstChild().getFirstChild().getChildNodes().item(1).getFirstChild().getFirstChild() == null)
                                {
                                    System.out.println("Semantic error: array bounds must be specified");
                                    error = true;
                                }
                                else
                                {
                                    tempToken =  dealWithExpression(statementsNode.getFirstChild().getFirstChild().getChildNodes().item(1).getFirstChild().getFirstChild().getFirstChild(), localTable);
                                    String boundsType = tempToken.getType();
                                    if(boundsType != "int")
                                    {
                                        System.out.println("Semantic error: array bounds must be integer type");
                                        error = true;
                                    }
                                    else
                                    {
                                        assignmentOp = statementsNode.getFirstChild().getFirstChild().getChildNodes().item(1).getChildNodes().item(1).getFirstChild().getFirstChild().getNodeName();
                                        
                                    }
                                }
                            }
                        }
                        else
                        {
                            if(type != IDtype.VARIABLE && type != IDtype.FUNCTION)
                            {
                                System.out.println("Semantic error: " + identifier + " is an array");
                                error = true;
                            }
                            else
                            {
                                assignmentOp = statementsNode.getFirstChild().getFirstChild().getChildNodes().item(1).getFirstChild().getFirstChild().getFirstChild().getNodeName();
                            }
                        }
                        
                        if(!error)
                        {
                            lookupElement = null;
                            traverseUntil(statementsNode.getFirstChild().getFirstChild().getChildNodes().item(1), "inputStatement");
                            IFOp option = getOption(assignmentOp);
                            if(!assignmentOp.equals("equal_op"))
                            {
                                if(!variableType.equals("int") && !variableType.equals("float"))
                                {
                                    System.out.println("Semantic error: special assignment operators can be used only with integer, float and string types");
                                } 
                            }

                            if(lookupElement != null)
                            {
                                // opcode for read
                                String value = getNewTempVar();
                                iFGenerator.addOpCode(IFOp.READ, variableType, "", value);
                                if(option != IFOp.MOV)
                                {
                                    if(type == IDtype.VARIABLE)
                                    {
                                        String tempVar = getNewTempVar();
                                        iFGenerator.addOpCode(option, identifier, value, tempVar);
                                        iFGenerator.addOpCode(IFOp.MOV, identifier, tempVar, "");
                                    }
                                    else
                                    {
                                        String tempVar = getNewTempVar();
                                        iFGenerator.addOpCode(option, identifier, value, tempVar);
                                        iFGenerator.addOpCode(IFOp.MOVA, identifier, tempToken.getTempVar(), tempVar);
                                    }
                                }
                                else
                                {
                                    if(type == IDtype.VARIABLE)
                                    {
                                        iFGenerator.addOpCode(IFOp.MOV, identifier, value, "");
                                    }
                                    else
                                    {
                                        iFGenerator.addOpCode(IFOp.MOVA, identifier, tempToken.getTempVar(), value);
                                    }
                                } 
                            }
                            else
                            {
                                Token token = null;
                                if(type == IDtype.ARRAY)
                                {
                                    token = dealWithExpression(statementsNode.getFirstChild().getFirstChild().getChildNodes().item(1).getChildNodes().item(1).getChildNodes().item(1).getFirstChild().getFirstChild(), localTable);
                                }
                                else
                                {
                                    token = dealWithExpression(statementsNode.getFirstChild().getFirstChild().getChildNodes().item(1).getFirstChild().getChildNodes().item(1).getFirstChild(), localTable);
                                }
                                if(isArray)
                                {
                                    //System.out.println("Semantic error: cannot use arrays in inicialisation");
                                }
                                String expressionType = token.getType();
                                
                                
                                
                                if(!expressionType.equals(variableType))
                                {
                                    System.out.println("Semantic error: variable and expression type doesnt match");
                                }
                                else
                                {
                                    if(option != IFOp.MOV)
                                    {   
                                        if(type == IDtype.VARIABLE)
                                        {
                                            String tempVar = getNewTempVar();
                                            iFGenerator.addOpCode(option, identifier, token.getTempVar(), tempVar);
                                            iFGenerator.addOpCode(IFOp.MOV, identifier, tempVar, "");
                                            localTable.setValue(identifier, tempVar);
                                        }
                                        else
                                        {
                                            String tempVar = getNewTempVar();
                                            iFGenerator.addOpCode(option, identifier, token.getTempVar(), tempVar);
                                            iFGenerator.addOpCode(IFOp.MOVA, identifier, tempToken.getTempVar(), tempVar);
                                        }
                                    }
                                    else
                                    {
                                        if(type == IDtype.VARIABLE)
                                        {
                                            iFGenerator.addOpCode(IFOp.MOV, identifier, token.getTempVar(), "");
                                            localTable.setValue(identifier, token.getTempVar());
                                        }
                                        else
                                        {
                                            iFGenerator.addOpCode(IFOp.MOVA, identifier, tempToken.getTempVar(), token.getTempVar());
                                        }
                                    }

                                }
                            }
                        }
                    }
                }
                else
                {   //function call
                    //statementsNode.getFirstChild().getFirstChild().getFirstChild() - function name
                    dealWithFunctionCall(statementsNode.getFirstChild().getFirstChild().getChildNodes().item(1).getFirstChild() , localTable, statementsNode.getFirstChild().getFirstChild().getFirstChild().getAttributes().item(0).getNodeValue(), 1);
                    
                }
                
            }
            else if (simpleStatement.equals("ioStatement"))
            {
                if(statementsNode.getFirstChild().getFirstChild().getFirstChild().getNodeName().equals("outputStatement"))
                {
                    Token token = dealWithExpression(statementsNode.getFirstChild().getFirstChild().getFirstChild().getFirstChild().getNextSibling(), localTable);
                    iFGenerator.addOpCode(IFOp.WRITE, token.getType(), token.getTempVar(), "");
                }
                else
                {
                    iFGenerator.addOpCode(IFOp.READ, "", "", "");
                }
   
            }
        }
        else if(statement.equals("controlStatement"))
        {
            String controlStatement = statementsNode.getFirstChild().getFirstChild().getNodeName();
            if(controlStatement.equals("forLoop"))
            {
                String forVariable = statementsNode.getFirstChild().getFirstChild().getChildNodes().item(1).getFirstChild().getAttributes().item(0).getNodeValue();
                if(localTable.isSymbol(forVariable, IDtype.VARIABLE) == -1)
                {
                    System.out.println("Semantic error: variable " + forVariable + " is not defined"); 
                }
                else
                {
                    if(localTable.getSymbol(localTable.isSymbol(forVariable)).getVarType() != "int")
                    {
                        System.out.println("Semantic error: variable must be integer type");
                    }
                }
                
                String label1 = getNewLabel();
                                
                //for expression
                Token forExpression = dealWithExpression(statementsNode.getFirstChild().getFirstChild().getChildNodes().item(3).getFirstChild(), localTable);
                String forExpressionType = forExpression.getType();
                String forExpressionValue = forExpression.getTempVar();
                iFGenerator.addOpCode(IFOp.MOV, forVariable, forExpressionValue, "");

                iFGenerator.addOpCode(IFOp.LABEL, "", "", label1);

                //to expression
                Token toExpression = dealWithExpression(statementsNode.getFirstChild().getFirstChild().getChildNodes().item(5).getFirstChild(), localTable);
                String toExpressionType = toExpression.getType();
                String toExpressionValue = toExpression.getTempVar();
                
                if(!toExpressionType.equals("int") || !forExpressionType.equals("int"))
                {
                    System.out.println("Semantic error: integer type expected in for loop");
                }

                if(statementsNode.getFirstChild().getFirstChild().getChildNodes().item(6) != null)
                {
                    
                    String label2 = getNewLabel();
                    String tempVar1 = getNewTempVar();
                                                   //0            //4
                    iFGenerator.addOpCode(IFOp.LE, forVariable, toExpressionValue, tempVar1);

                    iFGenerator.addOpCode(IFOp.JF, tempVar1, label2, "");

                    dealWithBlock(statementsNode.getFirstChild().getFirstChild().getChildNodes().item(6).getFirstChild(), localTable);
                    
                    String tempVar2 = getNewTempVar();
                    
                    iFGenerator.addOpCode(IFOp.LT, forVariable, toExpressionValue, tempVar1);

                    iFGenerator.addOpCode(IFOp.JF, tempVar1, label2, "");

                    
                    iFGenerator.addOpCode(IFOp.ADD, forVariable, "$1", tempVar2);
                    iFGenerator.addOpCode(IFOp.MOV, forVariable, tempVar2, "");
                    iFGenerator.addOpCode(IFOp.JMP, "", "", label1);
                    iFGenerator.addOpCode(IFOp.LABEL, "", "", label2);
                }
                
            }
            else if(controlStatement.equals("whileLoop"))
            {
                String label1 = getNewLabel();
                iFGenerator.addOpCode(IFOp.LABEL, "", "", label1);
                Token token = dealWithExpression(statementsNode.getFirstChild().getFirstChild().getChildNodes().item(1).getFirstChild() ,localTable);
                if(!token.getType().equals("bool"))
                {
                   System.out.println("Semantic error: boolean type expected");
                }
                
                if(statementsNode.getFirstChild().getFirstChild().getChildNodes().item(2) != null)
                {
                    String label2 = getNewLabel();
                    String tempVar = token.getTempVar();
                    
                    iFGenerator.addOpCode(IFOp.JF, tempVar, label2, "");
                    
                    dealWithBlock(statementsNode.getFirstChild().getFirstChild().getChildNodes().item(2).getFirstChild(), localTable);
                    
                    iFGenerator.addOpCode(IFOp.JMP, "", "", label1);
                    iFGenerator.addOpCode(IFOp.LABEL, "", "", label2); 
                }
                
            }
            else if(controlStatement.equals("ifStatement"))
            {
                Token token = dealWithExpression(statementsNode.getFirstChild().getFirstChild().getChildNodes().item(1).getFirstChild(), localTable);
                if(!token.getType().equals("bool"))
                {
                    System.out.println("Semantic error: boolean type expected");
                }
                lookupElement = null;
                traverseUntil(statementsNode.getFirstChild(), "block");

                String label = getNewLabel();
                String tempVar = token.getTempVar();
                
                iFGenerator.addOpCode(IFOp.JF, tempVar, label, "");

                if(lookupElement != null)
                {                      
                    dealWithBlock(lookupElement.getFirstChild(), localTable);
                }
                String label2 = getNewLabel();
                iFGenerator.addOpCode(IFOp.JMP, "", "", label2);
                iFGenerator.addOpCode(IFOp.LABEL, "", "", label);
                
                lookupElement = null;
                if(statementsNode.getFirstChild().getFirstChild().getChildNodes().item(3) != null)
                {
                    traverseUntil(statementsNode.getFirstChild().getFirstChild().getChildNodes().item(3), "block");
                }
                
                if(lookupElement != null) //else
                {
                    dealWithBlock(lookupElement.getFirstChild(), localTable); 
                }
                iFGenerator.addOpCode(IFOp.LABEL, "", "", label2);  
            }
        }
        else if(statement.equals("reservedStatement"))
        {
            Token token = null;

            if(statementsNode.getFirstChild().getFirstChild().getNextSibling() != null)
            {
                
                token = dealWithExpression(statementsNode.getFirstChild().getFirstChild().getNextSibling().getFirstChild(), localTable);
                
                if(returnType != null && !returnType.equals(token.getType()))
                {
                    System.out.println("Semantic error: return type and function type doesnt match");
                }
                returnType = token.getType();
                if(isArray)
                {
                    //System.out.println("Semantic error: cannot return an array");
                }
            }
            else
            {
                if(returnType != null && !returnType.equals("void"))
                {
                    System.out.println("Semantic error: return type and function type doesnt match");
                }
                returnType = "void";
            }
            if(token == null)
            {
                iFGenerator.addOpCode(IFOp.RET, "", "", "");
            }
            else
            {
                iFGenerator.addOpCode(IFOp.RET, token.getTempVar(), "", "");
            }
        }
        
        // @ next statement

        if(statementsNode.getNextSibling() != null)
        {
            dealWithBlock(statementsNode.getNextSibling(), localTable);
        }

    }

//-------------------------------------------------------------------------------------------------

    private static String dealWithFunctionCall(Node statementsNode, SymbolsTable localTable, String functionName, int option)
    {
        String functionType = null;
        if(symbolsTable.isSymbol(functionName, IDtype.FUNCTION) == -1)
        {
            System.out.println("Semantic error: function " + functionName + " is not defined");
        }
        else
        {
            functionType = symbolsTable.getSymbol(symbolsTable.isSymbol(functionName)).getVarType();
            //callVariableList

            if(statementsNode.getFirstChild() != null)
            {
                String funcParams[] = symbolsTable.getSymbol(symbolsTable.isSymbol(functionName)).getAdditionalInfo();
                
                ArrayList<String> params = new ArrayList<String>();
                if(option == 0)
                {
                    checkCallVariableList(statementsNode.getFirstChild(), localTable, params);
                }
                else
                {
                    checkCallVariableList1(statementsNode.getFirstChild(), localTable, params);
                }
                
                int paramsCount = params.size();
                
                if(funcParams == null || funcParams.length != paramsCount)
                {
                    System.out.println("Semantic error: function parameters count doesnt match");
                }
                else
                {
                    for(int i = 0; i < paramsCount; i++)
                    {
                        if(!funcParams[i].equals(params.get(i)))
                        {
                            System.out.println("Semantic error: function parameters types doesnt match");    
                        }
                    }
                } 
            }
            else
            {
                if(symbolsTable.getSymbol(symbolsTable.isSymbol(functionName)).getAdditionalInfo() != null)
                {
                    System.out.println("Semantic error: function parameters types doesnt match"); 
                }
            }
            if(functionType.equals("void"))
            {
                iFGenerator.addOpCode(IFOp.CALL, "", "", functionName);
            }
            else
            {
                iFGenerator.addOpCode(IFOp.CALL, getNewTempVar(), "", functionName);
            }
            
        }

        return functionType;
    }

//-------------------------------------------------------------------------------------------------

    private static void dealWithBlock(Node blockNode, SymbolsTable localTable)
    {
        SymbolsTable tempTable = new SymbolsTable();
        for(int i = 0; i < localTable.getSymbolsCount(); i++)
        {
            tempTable.add(localTable.getSymbol(i));
        }
        dealWithStatements(blockNode.getFirstChild(), tempTable);
    }

//-------------------------------------------------------------------------------------------------

 private static void checkCallVariableList1(Node callVariableNode, SymbolsTable localTable, ArrayList<String> params)
    {
        Token token = dealWithExpression(callVariableNode.getFirstChild(), localTable);
        String param = token.getType();

        if(isArray)
        {
            param += ':' + IDtype.ARRAY.toString();
            iFGenerator.addOpCode(IFOp.PARAM, "*", "", token.getTempVar());
        }
        else
        {
            param += ':' + IDtype.VARIABLE.toString();
            iFGenerator.addOpCode(IFOp.PARAM, "", "", token.getTempVar());
        }
      
        params.add(param);
      
        if(callVariableNode.getChildNodes().item(1) != null)
        {
            checkCallVariableList1(callVariableNode.getChildNodes().item(1), localTable, params);
        }        
    }

//-------------------------------------------------------------------------------------------------
    

    private static void checkCallVariableList(Node callVariableNode, SymbolsTable localTable, ArrayList<String> params)
    {
        Token token = null;

        if(callVariableNode.getChildNodes().item(1) != null)
        {
            token = dealWithExpression(callVariableNode.getChildNodes().item(1), localTable);
        }
        else
        {
            token = dealWithExpression(callVariableNode.getFirstChild(), localTable);
        }
        

        String param = token.getType();

        if(isArray)
        {
            param += ':' + IDtype.ARRAY.toString();
            iFGenerator.addOpCode(IFOp.PARAM, "*", "", token.getTempVar());
        }
        else
        {
            param += ':' + IDtype.VARIABLE.toString();
            iFGenerator.addOpCode(IFOp.PARAM, "", "", token.getTempVar());
        }
      
        params.add(param);
      
        if(!callVariableNode.getFirstChild().getNodeName().equals("expression"))
        {
            checkCallVariableList(callVariableNode.getFirstChild(), localTable, params);
        }        
    }

//-------------------------------------------------------------------------------------------------
    
    private static boolean isArray = false;
    private static Token dealWithExpression(Node expressionNode, SymbolsTable localTable)
    {
       if(isArray)
       {
           //System.out.println("Semantic error: cant do operations with array");
       }
       isArray = false;
       return dealWithExpression0(expressionNode, localTable);
    }
    
//-------------------------------------------------------------------------------------------------

    private static Token dealWithExpression0(Node expressionNode, SymbolsTable localTable)
    {
        lookupElement = expressionNode;
        while(lookupElement.getFirstChild() != null && !lookupElement.getNodeName().equals("variableOrFuncCall"))
        {
  //          System.out.println("eee: " + lookupElement);
            lookupElement = lookupElement.getFirstChild();
             //System.out.println("eee: " + lookupElement);
         
        }
        Node lookupCopy = lookupElement;
    //System.out.println("bbb: " + lookupElement);
        
        //for array
        String bounds = null;
        String tempVar = null;
        String array = null;

        String operandType = null;
        String operandValue = null;

        if(lookupCopy.getNodeName().equals("variableOrFuncCall"))
        {
            if(lookupCopy.getFirstChild().getNodeName().equals("identifier"))
            {
                if(localTable.isSymbol(lookupCopy.getFirstChild().getAttributes().item(0).getNodeValue(), IDtype.VARIABLE) == -1)
                {
                    System.out.println("Semantic error: variable " + lookupCopy.getFirstChild().getAttributes().item(0).getNodeValue() + " is not defined");
                }
                else
                {
                    operandType = localTable.getSymbol(localTable.isSymbol(lookupCopy.getFirstChild().getAttributes().item(0).getNodeValue(), IDtype.VARIABLE)).getVarType();
                    operandValue = lookupCopy.getFirstChild().getAttributes().item(0).getNodeValue();
                }
            } // array or funcCall
            else if(lookupCopy.getFirstChild().getFirstChild().getNodeName().equals("arrayVariable"))
            {
                if(localTable.isSymbol(lookupCopy.getChildNodes().item(1).getAttributes().item(0).getNodeValue(), IDtype.ARRAY) == -1)
                {
                    System.out.println("Semantic error: array " + lookupCopy.getChildNodes().item(1).getAttributes().item(0).getNodeValue() + " is not defined");
                }
                else
                {
                    //array
                    if(lookupCopy.getFirstChild().getFirstChild().getFirstChild() != null)
                    {
                        Token token = null;
                        
                        token = dealWithExpression(lookupCopy.getFirstChild().getFirstChild().getFirstChild().getFirstChild(), localTable);
                        bounds = token.getTempVar();
                        tempVar = getNewTempVar();
                        array = lookupCopy.getChildNodes().item(1).getAttributes().item(0).getNodeValue();
                        iFGenerator.addOpCode(IFOp.PULL, array, bounds, tempVar);
                        
                        operandValue = tempVar;
                        operandType = localTable.getSymbol(localTable.isSymbol(lookupCopy.getChildNodes().item(1).getAttributes().item(0).getNodeValue())).getVarType();

                        if(!token.getType().equals("int"))
                        {
                            System.out.println("Semantic error: array bounds must be integer type");
                        }
                    }
                    else
                    {
                        isArray = true;
                        operandValue = localTable.getSymbol(localTable.isSymbol(lookupCopy.getChildNodes().item(1).getAttributes().item(0).getNodeValue())).getName();
                        operandType = localTable.getSymbol(localTable.isSymbol(lookupCopy.getChildNodes().item(1).getAttributes().item(0).getNodeValue())).getVarType();
                        
                        while(lookupCopy != expressionNode)
                        {
                            if(lookupCopy.getNextSibling() != null)
                            {
                                //System.out.println("Semantic error: cant do operations with array");
                                break;
                            }
                            lookupCopy = lookupCopy.getParentNode(); 
                        }
  
                    }
                }
            
            }
            else
            {
                if(symbolsTable.isSymbol(lookupCopy.getChildNodes().item(1).getAttributes().item(0).getNodeValue(), IDtype.FUNCTION) == -1)
                {
                    System.out.println("Semantic error: function " + lookupCopy.getChildNodes().item(1).getAttributes().item(0).getNodeValue() + " is not defined");
                }
                else
                {
                    String functionType = dealWithFunctionCall(lookupCopy.getFirstChild().getFirstChild(), localTable, lookupCopy.getChildNodes().item(1).getAttributes().item(0).getNodeValue(), 0);
                    if(!functionType.equals("void"))
                    {
                        operandValue = "_t" + Integer.toString(tempVariable - 1);
                    }
                    operandType = functionType;
                }
            }
        
        }
        else
        {
            operandType = getType(lookupCopy.getNodeName());
            if(lookupCopy.getAttributes().item(0) != null)
            {
               operandValue = '$' + lookupCopy.getAttributes().item(0).getNodeValue();
            }
            else
            {
               operandValue = "$"; 
            }
            
        }
        
        String operator = null;

        Token tempToken = null;
        
        if(operandValue.equals("$true"))
        {
            operandValue = "$1";
        
        }
        else if(operandValue.equals("$false"))
        {
            operandValue = "$0";
        }
        
        Node tmpNode = lookupCopy;
        Token returnToken = new Token(operandValue, operandType);
        Token newTempToken;

        //System.out.println("Parent node: "+expressionNode.getParentNode());
        while(tmpNode != null && tmpNode != expressionNode.getParentNode())
        {          
           // System.out.println("tmpNode: "+ tmpNode);
            if(tmpNode.getNextSibling() != null && tmpNode.getNextSibling().getFirstChild() != null && getIFOp(tmpNode.getNextSibling().getFirstChild().getNodeName()) != null)
            {
                operator = tmpNode.getNextSibling().getFirstChild().getNodeName();
                //System.out.println("operatorius: "+tmpNode.getNextSibling().getFirstChild().getNodeName());
                
                tmpNode = tmpNode.getParentNode();
                //System.out.println("gauti kita: "+ tmpNode.getNextSibling());
                newTempToken = new Token(null, null);

                tempToken = dealWithExpression0(tmpNode.getNextSibling(), localTable);
//System.out.println("--op: "+ operator+", tempToken: "+tempToken.getTempVar()
                //        +", returnTokrn: "+returnToken.getTempVar()+", newTempToken: "+newTempToken.getTempVar());
                addOpCode(operator, tempToken, returnToken, newTempToken);
                //System.out.println("--op: "+ operator+", tempToken: "+tempToken.getTempVar()
                //        +", returnTokrn: "+returnToken.getTempVar()+", newTempToken: "+newTempToken.getTempVar());
                returnToken.setTempVar(newTempToken.getTempVar());
                returnToken.setType(newTempToken.getType());
            }

            tmpNode = tmpNode.getParentNode(); 
            
        }

        return returnToken;
    }    
//------------------------------------------------------------------------------------------------- 

    private static void addOpCode(String operator, Token firstToken, Token secondToken, Token resultToken)
    {
        IFOp op = getIFOp(operator);
        String firstOp;
        String secondOp;
        String result;
        
        if((firstToken.getType().equals("int") && secondToken.getType().equals("float")) ||
            (firstToken.getType().equals("float") && secondToken.getType().equals("int")))
        {
            resultToken.setType("float");
        }
        else if((!firstToken.getType().equals(secondToken.getType()))) //&&
            //(firstToken.getType().equals("string") && !firstToken.getType().equals("char")))
        {
            System.out.println("Semantic error: operands type doesnt match");
        }
        else
        {
            resultToken.setType(firstToken.getType());
        }
        
        if(operator.equals("mod_op") && (!firstToken.getType().equals("int") || !secondToken.getType().equals("int")))
        {
            System.out.println("Semantic error: mod operator can be used only for integer types");
        }
    
        resultToken.setTempVar(getNewTempVar());
        
        if(operator.equals("less_op") || operator.equals("less_equal_op") || 
            operator.equals("greater_op") || operator.equals("greater_equal_op") ||
            operator.equals("div_op") || operator.equals("minus_op") ||
            operator.equals("mul_op"))
        {
            if(!firstToken.getType().equals("int") && !firstToken.getType().equals("float"))
            {
                System.out.println("Semantic error: arithmetic operators can be usend only with integer and float types");
            }
        }
        
        if(operator.equals("plus_op"))
        {
            if(firstToken.getType().equals("bool") || firstToken.getType().equals("char"))
            {
                System.out.println("Semantic error: plus operator is used in wrong way");
            }
        }
        
        
        if(operator.equals("less_op") || operator.equals("less_equal_op") ||
            operator.equals("not_equal_op") || operator.equals("equal_equal_op") ||
            operator.equals("greater_op") || operator.equals("greater_equal_op"))
        {
            resultToken.setType("bool");
        }

        firstOp =  firstToken.getTempVar();

        secondOp = secondToken.getTempVar();
        
        result = resultToken.getTempVar();
        if(firstToken.getType().equals("string") && operator.equals("plus_op"))
        {
            iFGenerator.addOpCode(IFOp.CONCAT, firstOp, secondOp, result);
        }
        else
        {
            iFGenerator.addOpCode(op, firstOp, secondOp, result);
        }
         
    }

//-------------------------------------------------------------------------------------------------   

    private static boolean traverseUntilTerminal(Node node)
    {
        found = false;

        if(terminalsHolder.isTerminal("<" + node.getNodeName() + ">") != -1)
        {
            lookupElement = node;
            found = true;
            return true;
        }

        if(!found)
        {
            for(int i = 0; i < node.getChildNodes().getLength(); i++)
            {
                if(traverseUntilTerminal(node.getChildNodes().item(i)))
                {
                    return true;
                }
            }
        }
        return false;
    }

//-------------------------------------------------------------------------------------------------

    private static String getType(String value)
    {
    	if(value.equals("int_const"))
    	{
    	    return "int";
    	}
    	else if(value.equals("float_const"))
    	{
    	    return "float";
    	}
    	else if(value.equals("char_const"))
    	{
    	    return "char";
    	}
    	else if(value.equals("string_const"))
    	{
    	    return "string";
    	}
    	else
    	{
    	    return "bool";
    	}
    }
        
//-------------------------------------------------------------------------------------------------
    
    private static IFOp getOption(String option)
    {
        if(option.equals("equal_op"))
        {
            return IFOp.MOV;
        }
        else if(option.equals("plus_equal_op"))
        {
            return IFOp.ADD;
        }
        else if(option.equals("minus_equal_op"))
        {
            return IFOp.SUB;
        }
            else if(option.equals("mul_equal_op"))
        {
            return IFOp.MUL;
        }
        else if(option.equals("div_equal_op"))
        {
            return IFOp.DIV;
        }
        else if(option.equals("mod_equal_op"))
        {
            return IFOp.MOD;
        }
        return null;    
    }

//-------------------------------------------------------------------------------------------------
    
    private static int tempLabel = 0;
    private static String getNewLabel()
    {
        return "L" + Integer.toString(tempLabel++);
    }
    
//-------------------------------------------------------------------------------------------------
    
    private static int tempVariable = 0;
    private static String getNewTempVar()
    {
        return "_t" + Integer.toString(tempVariable++);
    }
    
//-------------------------------------------------------------------------------------------------

    private static IFOp getIFOp(String op)
    {
        if(op.equals("plus_op"))
        {
            return IFOp.ADD;
        }
        else if(op.equals("minus_op"))
        {
            return IFOp.SUB;
        }
        else if(op.equals("mod_op"))
        {
            return IFOp.MOD;
        }
        else if(op.equals("div_op"))
        {
            return IFOp.DIV;
        }
        else if(op.equals("mul_op"))
        {
            return IFOp.MUL;
        }
        else if(op.equals("equal_equal_op"))
        {
            return IFOp.EE;
        }
        else if(op.equals("not_equal_op"))
        {
            return IFOp.NE;
        }
        else if(op.equals("greater_equal_op"))
        {
            return IFOp.GE;
        }
        else if(op.equals("greater_op"))
        {
            return IFOp.GT;
        }
        else if(op.equals("less_op"))
        {
            return IFOp.LT;
        }
        else if(op.equals("less_equal_op"))
        {
            return IFOp.LE;
        }
        else if(op.equals("or_op"))
        {
            return IFOp.OR;
        }
        else if(op.equals("and_op"))
        {
            return IFOp.AND;
        }
        return null;
    }
   
}