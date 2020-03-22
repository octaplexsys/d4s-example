package leaderboard.dynamo.d4s

import java.util.UUID

import d4s.config.DynamoMeta
import d4s.models.table._
import io.circe.Codec
import io.circe.derivation.deriveCodec
import leaderboard.models.common.UserId
import leaderboard.models.{UserProfile, UserProfileWithId}
import software.amazon.awssdk.services.dynamodb.model.AttributeValue

final class ProfilesTable(implicit meta: DynamoMeta) extends TableDef {
  private[this] val mainKey = DynamoKey(hashKey = DynamoField[UUID]("userId"))

  override val table: TableReference = TableReference("d4s-profile-table", mainKey)

  override val ddl: TableDDL = TableDDL(table)

  def mainFullKey(userId: UserId): Map[String, AttributeValue] = {
    mainKey.bind(userId.value)
  }
}

object ProfilesTable {
  final case class UserProfileWithIdStored(userId: UUID, userName: String, description: String) {
    def toAPI: UserProfileWithId = UserProfileWithId(UserId(userId), UserProfile(userName, description))
  }
  object UserProfileWithIdStored {
    implicit val codec: Codec.AsObject[UserProfileWithIdStored] = deriveCodec[UserProfileWithIdStored]
  }
}
