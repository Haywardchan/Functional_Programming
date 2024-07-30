package ass3

import java.beans.Expression

type Data = Any

case class Lambda(f: PartialFunction[List[Data], Data])

case class Instance(classN: String, name: List[String], value: List[Any])
