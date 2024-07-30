



final class test$u002Eworksheet$_ {
def args = test$u002Eworksheet_sc.args$
def scriptPath = """test.worksheet.sc"""
/*<script>*/
def f1: Int =
  if true then 0
  1

def f2: Int =
  if true then return 0
  1

// def f3: Int =
//   if true then 0

// def f4: Int =
//   if true then return 0

def f5: Int =
  if true then 0
  else 1

println("Hello, world!")
   
val x = 1
x + x

f1
f2
f5
/*</script>*/ /*<generated>*/
/*</generated>*/
}

object test$u002Eworksheet_sc {
  private var args$opt0 = Option.empty[Array[String]]
  def args$set(args: Array[String]): Unit = {
    args$opt0 = Some(args)
  }
  def args$opt: Option[Array[String]] = args$opt0
  def args$: Array[String] = args$opt.getOrElse {
    sys.error("No arguments passed to this script")
  }

  lazy val script = new test$u002Eworksheet$_

  def main(args: Array[String]): Unit = {
    args$set(args)
    script.hashCode() // hashCode to clear scalac warning about pure expression in statement position
  }
}

export test$u002Eworksheet_sc.script as test$u002Eworksheet

