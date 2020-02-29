package leaderboard.repo

import izumi.functional.bio.BIOMonad
import leaderboard.models.{QueryFailure, RankedProfile, UserId}

trait Ranks[F[_, _]] {
  def getRank(userId: UserId): F[QueryFailure, Option[RankedProfile]]
}
object Ranks {
  final class Impl[F[+_, +_]: BIOMonad](
    ladder: Ladder[F],
    profiles: Profiles[F],
  ) extends Ranks[F] {

    override def getRank(userId: UserId): F[QueryFailure, Option[RankedProfile]] = {
      for {
        maybeProfile <- profiles.getProfile(userId)
        scores       <- ladder.getScores
        res = for {
          profile <- maybeProfile
          rank    = scores.indexWhere(_.id == userId) + 1
          score   <- scores.find(_.id == userId).map(_.score)
        } yield RankedProfile(
          name        = profile.name,
          description = profile.description,
          rank        = rank,
          score       = score,
        )
      } yield res
    }

  }
}