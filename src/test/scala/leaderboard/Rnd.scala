package leaderboard

import izumi.functional.bio.{BIO, F}
import leaderboard.models.common.{Score, UserId}
import org.scalacheck.Gen.Parameters
import org.scalacheck.{Arbitrary, Gen, Prop}

trait Rnd[F[_, _]] {
  def apply[A: Arbitrary]: F[Nothing, A]
}

object Rnd {
  final class Impl[F[+_, +_]: BIO] extends Rnd[F] {
    override def apply[A: Arbitrary]: F[Nothing, A] = {
      F.sync {
        val (p, s) = Prop.startSeed(Parameters.default)
        Arbitrary.arbitrary[A].pureApply(p, s)
      }
    }
  }


  implicit val arbitraryUserId: Arbitrary[UserId] = Arbitrary(Gen.uuid.map(UserId(_)))
  implicit val arbitraryScore: Arbitrary[Score] = Arbitrary(Gen.posNum[Long].map(Score(_)))
}
