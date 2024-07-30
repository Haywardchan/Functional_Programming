package ass3

trait Interpreter:
  self: LispImpl =>
  import language.deprecated.symbolLiterals

  case class Delay(expr: Data)(using env: Environment[Data])(using tydefs: Environment[List[String]]) {
    lazy val value: Data = eval(expr)(using env)(using tydefs)
  }

  def eval(x: Data)(using env: Environment[Data])(using tydefs: Environment[List[String]]): Data = 
    // println(x)
    x match
    case _: String => x
    case _: Int => x
    case Symbol(name) =>
      var res = env.lookup(name)
      res match
        case delay: Delay => 
          // print(s"evaluated once! value: ${delay.value}\n")
          delay.value
        case _ => res
    case 'val :: param :: expr :: rest :: Nil =>
      eval(rest)(using env.extend(paramName(param), Delay(expr)(using env)(using tydefs)))
    case 'def :: param :: expr :: rest :: Nil =>
      eval(rest)(using env.extendRec(paramName(param), env1 => eval(expr)(using env1)))
    case 'if :: cond :: thenpart :: elsepart :: Nil =>
      if eval(cond) != 0 then eval(thenpart)
      else eval(elsepart)
    case 'quote :: y :: Nil => y
    case 'lambda :: params :: body :: Nil =>
      mkLambda(asList(params).map(paramName), body)
    case 'class :: nameAndfield :: rest :: Nil =>
      val class_name = className(asList(nameAndfield).head) 
      val field_name = asList(nameAndfield).tail.map(fieldName)
      val extendtydef = tydefs.extend(class_name, field_name)
      eval(rest)(using env.extend(class_name, field_name))(using extendtydef)
    case 'sel :: expr :: field :: Nil=> 
      eval(expr) match
      case Instance(classN, name, value) => 
        val index = name.indexOf(fieldName(field))
        if index != -1 then 
          value.apply(index) match
          case delay: Delay => eval(delay.value)
          case _ => eval(value.apply(index))
        else throw FieldError(s"class $classN has no field $field")
      case o => throw SelError(s"selection from a non-object: $o")
    case 'case :: scrut :: branches =>  
      val v = eval(scrut)
      def rec(branches: List[Data]): Any = branches match
        case Nil => throw SyntaxError(s"invalid case branch: ${branches.head}")
        case head :: tail =>
          head match
            case nameAndparam :: expr :: Nil => 
              nameAndparam match
                case _:List[_] =>
                  val classN = className(asList(nameAndparam).head)
                  val param = asList(nameAndparam).tail
                  val fieldNames = tydefs.lookup(classN)
                  v match
                  case Instance(class_name, name, value) => 
                    if classN == class_name then 
                      if (param.length != fieldNames.length) {
                        throw ClassArityMismatch(s"Wrong arity for class $classN")
                      }
                      eval(expr)(using env.extendMulti(param.map(paramName), value))(using tydefs)
                    else rec(tail)
                  case _ => throw MatchError(s"match error on: $scrut, v is $v")
                case _:Symbol =>
                  eval(expr)(using env.extend(paramName(nameAndparam), v))(using tydefs)
                case _ => throw SyntaxError(s"invalid case branch: ${branches.head}")
      rec(branches)

    case operator :: operands => 
      val inbuilt = List("=", "+", "-", "*", "/", "nil", "cons", "car", "cdr", "null?")
      eval(operator) match
        case Lambda(f) => 
          if inbuilt.contains(paramName(operator)) then f(operands.map(eval))
          else f(operands.map(op => Delay(op)))
        case classname :: args =>
          val classN = className(operator)
          val fieldNames = tydefs.lookup(classN)
          if (operands.length != fieldNames.length) {
            throw ClassArityMismatch(s"Wrong arity for class $classN")
          }
          //create object
          val objectInstance = Instance(classN, fieldNames, operands.map(op => Delay(op)))
          objectInstance
        case x => throw AppError("application of a non-function: " + x + " to " + operands +"::" + operator)
      
  def evaluate(x: Data): Data = eval(x)(using globalEnv)(using emptyEnvironment)

  def evaluate(s: String): String = lisp2string(evaluate(string2lisp(s)))
