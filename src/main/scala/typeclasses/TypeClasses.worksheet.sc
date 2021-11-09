
object XXX:
  given Int = 1234
  given Double = 12.0d
  val bla = 3.0d

  def apply() = new XXX

class XXX:
  import XXX.{*, given}
  // given Int = 23
  def mpyByFactor(n: Int)(using factor: Int): Int = n * factor

  object YYY:
    // given Int = 10
    val theIntegerWeAreLookingFor: Int = summon[Int]
    val x = mpyByFactor(9)
    val d = bla
  end YYY
end XXX

val instanceXXX = XXX()

instanceXXX.YYY.x
// If we comment out the Given instances for Int in class XXX and object YYY, we 
// observe that the given instance defined in XXX's companion object is picked-up
instanceXXX.YYY.theIntegerWeAreLookingFor

import java.util.UUID

val uuid: UUID = UUID.randomUUID

// Let's define two extension methods on UUID
extension [A](x: UUID)
  def reversedToString[B]: String = x.toString.reverse
  def reversedToStringRepeatBy(repeat: Int): String = reversedToString * repeat
end extension

uuid.reversedToString[Double]
uuid.reversedToStringRepeatBy(3)

// A very simple example of a type class
trait Show[A] {
  def show(a: A): String
}

// Defining a Show type class instance for a Boolean
given Show[Boolean] with {
  def show(b: Boolean): String =
    if (b) "Boolean: true" else "Boolean: false"
}

// Defining a Show type class instance for an Int
given Show[Int] with {
  override def show(n: Int): String =
    s"Int: $n"
}

// Looking up a particular instance of a type class (Show[Int] in this case)
// Lots of typing and not very useful in itself...
implicitly[Show[Int]].show(23)

// Making type classes easy to use by defining an extension
extension [A : Show](x: A)
  def show: String = summon[Show[A]].show(x)

// ... and using it
123.show
true.show

// 
given [A : Show]: Show[List[A]] with {
  def show(ls: List[A]): String = 
    val element = ls.map(_.show).mkString(", ")
    s"List($element)"
}

// Defining a type class instance for arbitrary tuples with 2 elements
given [A: Show, B: Show]: Show[(A, B)] with {
  def show(tup: (A, B)): String =
    s"Tuple2(${tup(0).show}, ${tup(1).show})"
}


List(1,2,3).show

List(List(1,2), List(3,4)).show

(false, 1).show

(List(List(true, false)), (1, false)).show

// @Vidal and @Manish. After I showed you this, I went back and looked
// at the documentation and it turns out that things can be expressed
// even more compact by incorporating the extension method in the type
// class definition

trait Showable[A]:
  extension(a: A) def show: String

case class Person(firstName: String, lastName: String)

given Showable[Person] with
  extension(p: Person) def show: String =
    s"${p.firstName} ${p.lastName}"

given [A: Showable]: Showable[Seq[A]] with {
  extension(xs: Seq[A]) def show: String =
    val xss = xs.map(_.show).mkString(", ")
    s"Seq($xss)"
}
  
Vector(
  Person("Eric", "Loots"),
  Person("Manish", "Ghildiyal"),
  Person("Vidal", "Gonzalez P")
).show