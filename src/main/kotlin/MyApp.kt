import com.slack.api.bolt.App
import com.slack.api.bolt.context.builtin.EventContext
import com.slack.api.bolt.socket_mode.SocketModeApp
import com.slack.api.model.event.MessageEvent
import com.slack.api.model.kotlin_extension.view.blocks
import com.slack.api.model.view.Views.*


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

    // Global Shortcut
    app.globalShortcut("socket-mode-shortcut") { req, ctx ->
        val modalView = view { v -> v
            .type("modal")
            .callbackId("modal-id")
            .title(viewTitle {it.type("plain_text").text("タスク登録")})
            .submit(viewSubmit {it.type("plain_text").text("送信")})
            .close(viewClose { it.type("plain_text").text("キャンセル") })
            .blocks {
                input {
                    blockId("input-task")
                    element {
                        plainTextInput {
                            actionId("input")
                            multiline(true)
                        }
                    }
                    label("タスクの詳細・期限などを書いてください")
                }
                input {
                    blockId("user-select")
                    element {
                        multiUsersSelect {
                            actionId("multi-select")
                            maxSelectedItems(3)
                        }
                    }
                    label("ユーザーを選択してください")
                }
            }
        }
        ctx.asyncClient().viewsOpen { it.triggerId(req.payload.triggerId).view(modalView) }
        ctx.ack()
    }
    app.viewSubmission("modal-id") { req, ctx ->
        ctx.logger.info("Submitted data: {}", req.payload.view.state.values)
        ctx.ack()
    }

    // Start SocketMode
    SocketModeApp(app).start()
}

class SlackClient(val eventContext: EventContext) {

    fun createChannel() {
        eventContext.client().conversationsCreate { it.name("war room channel") }
    }

}