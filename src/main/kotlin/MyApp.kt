import com.slack.api.bolt.App
import com.slack.api.bolt.socket_mode.SocketModeApp
import com.slack.api.methods.MethodsClient
import com.slack.api.model.event.MessageEvent
import com.slack.api.model.kotlin_extension.view.blocks
import com.slack.api.model.view.Views.*
import me.komurohiraku.service.AbstractUuidGenerator
import me.komurohiraku.service.UuidGenerator
import me.komurohiraku.service.UuidType
import java.time.LocalDate


fun main() {

    // App Instance
    val app = App()

    // Event API
    app.event(MessageEvent::class.java) { req, ctx ->
        // req.event.user -> ユーザー名。@つけるとメンションになるっぽい　
        // req.event.text -> 入力されたテキスト
        ctx.say("こんにちは <@" + req.event.user + ">! " + req.event.text)
        ctx.ack()
    }

    // slash command https://slack.dev/java-slack-sdk/guides/ja/slash-commands
    app.command("/uuid") { req, ctx ->
        val arguments = req.payload.text.split(" ")
        ctx.logger.info("$arguments, ${arguments.size}")

        var type = when (arguments.size) {
            1 -> if (arguments.isEmpty()) {
                arguments[0].toUpperCase()
            } else {
                AbstractUuidGenerator.DEFAULT_TYPE
            } else -> {
                ctx.logger.error("Failed Argument $arguments, Set default")
                AbstractUuidGenerator.DEFAULT_TYPE
            }
        }
        ctx.logger.info("input type: $type")

        // java.lang.IllegalArgumentException がスローされる可能性
        val uuid: UuidGenerator = AbstractUuidGenerator.from(UuidType.valueOf(type))
        ctx.say("${uuid.generate()}")
        ctx.ack()
    }

    // Global Shortcut
    app.globalShortcut("socket-mode-shortcut") { req, ctx ->
        val modalView = view { v -> v
            .type("modal")
            .callbackId("modal-id")
            .title(viewTitle {it.type("plain_text").text("Create War room")})
            .submit(viewSubmit {it.type("plain_text").text("作成")})
            .close(viewClose { it.type("plain_text").text("閉じる") })
            .blocks {
                input {
                    blockId("input-environment")
                    element {
                        plainTextInput {
                            actionId("input-env")
                            multiline(false)
                        }
                    }
                    label("障害対応環境")
                }
                input {
                    blockId("input-description")
                    element {
                        plainTextInput {
                            actionId("input-desc")
                            multiline(true)
                        }
                    }
                    label("障害対応チャンネルの詳細を入力")
                }
                input {
                    blockId("commander-select")
                    element {
                        usersSelect {
                            actionId("commander-selected")
                        }
                    }
                    label("司令塔を選択してください")
                }
                input {
                    blockId("operator-select")
                    element {
                        usersSelect {
                            actionId("operator-selected")
                        }
                    }
                    label("メイン対応者を選択してください")
                }
                input {
                    blockId("recorder-select")
                    element {
                        usersSelect {
                            actionId("recorder-selected")
                        }
                    }
                    label("記録係を選択してください")
                }
            }
        }
        ctx.asyncClient().viewsOpen { it.triggerId(req.payload.triggerId).view(modalView) }
        ctx.ack()
    }

    // Submit動作を制御
    app.viewSubmission("modal-id") { req, ctx ->
        ctx.logger.info("Submitted data: {}", req.payload.view.state.values)
        // create conversation
        val vals = req.payload.view.state.values.get("input-environment")?.get("input-env")?.value
        ctx.logger.info("vals = $vals")
        val client = SlackClient(ctx.client(), ctx.botToken, vals)
        ctx.logger.info("Created Channel: {}", client.createChannel())
        ctx.ack()
    }

    // Start SocketMode
    SocketModeApp(app).start()
}

class SlackClient(val client: MethodsClient, val botToken: String, val environmentName: String?) {

    fun createChannel():Boolean {
        val date = LocalDate.now()
        val response = client.conversationsCreate { it
            .name("$date-$environmentName-warroom")
            .token(botToken)
            .isPrivate(false)
        }

        return response.isOk
    }
}