package org.bsc.langgraph4j.spring.ai.agentexecutor;

import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.tool.ToolCallback;

import java.util.List;

public interface ChatService {

    ChatResponse execute(List<Message> messages );

    List<ToolCallback> tools();
}
