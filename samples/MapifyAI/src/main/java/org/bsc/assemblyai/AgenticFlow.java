package org.bsc.assemblyai;

import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.data.message.UserMessage;
import org.bsc.assemblyai.actions.ExtractKeypointsFromTranscript;
import org.bsc.assemblyai.actions.GeneratePlantUMLImage;
import org.bsc.assemblyai.actions.GeneratePlantUMLMindmap;
import org.bsc.langgraph4j.GraphStateException;
import org.bsc.langgraph4j.StateGraph;
import org.bsc.langgraph4j.langchain4j.serializer.std.AiMessageSerializer;
import org.bsc.langgraph4j.langchain4j.serializer.std.UserMessageSerializer;
import org.bsc.langgraph4j.serializer.StateSerializer;
import org.bsc.langgraph4j.serializer.std.ObjectStreamStateSerializer;
import org.bsc.langgraph4j.state.AgentState;
import org.springframework.stereotype.Service;

import java.util.Map;

import static org.bsc.langgraph4j.StateGraph.END;
import static org.bsc.langgraph4j.StateGraph.START;
import static org.bsc.langgraph4j.action.AsyncNodeAction.node_async;

/**
 * The `AgenticFlow` class is a service that orchestrates the flow of an agent based on generated keypoint transcripts, plantUML mindmaps, and related images.
 *
 * @author SystemName (Generated by AI)
 */
@Service
public class AgenticFlow {

    /**
     * Nested class representing a state within the agent's flow. Extends `AgentState` and provides serialization capabilities using a custom `StateSerializer`.
     */
    public static class State extends AgentState {

        /**
         * Constructs a new instance of `State`.
         *
         * @param initData Initial data used to initialize the state.
         */
        public State(Map<String, Object> initData) {
            super(initData);
        }

        /**
         * Provides a serializer for instances of this `State` class using an object stream approach.
         *
         * @return An instance of `StateSerializer` for serializing and deserializing `State` objects.
         */
        public static StateSerializer<State> serializer() {
            var serializer = new ObjectStreamStateSerializer<>(State::new);
            serializer.mapper().register(UserMessage.class, new UserMessageSerializer());
            serializer.mapper().register(AiMessage.class, new AiMessageSerializer());
            return serializer;
        }
    }

    /**
     * Final field holding a reference to an instance of `ExtractKeypointsFromTranscript`.
     */
    final ExtractKeypointsFromTranscript extractKeypointsFromTranscript;

    /**
     * Final field holding a reference to an instance of `GeneratePlantUMLMindmap`.
     */
    final GeneratePlantUMLMindmap generatePlantUMLMindmap;

    /**
     * Final field holding a reference to an instance of `GeneratePlantUMLImage`.
     */
    final GeneratePlantUMLImage generatePlantUMLImage;

    /**
     * Constructs a new instance of `AgenticFlow` with the provided dependencies.
     *
     * @param extractKeypointsFromTranscript Instance of `ExtractKeypointsFromTranscript`.
     * @param generatePlantUMLMindmap      Instance of `GeneratePlantUMLMindmap`.
     * @param generatePlantUMLImage        Instance of `GeneratePlantUMLImage`.
     */
    public AgenticFlow( ExtractKeypointsFromTranscript extractKeypointsFromTranscript,
                        GeneratePlantUMLMindmap generatePlantUMLMindmap,
                        GeneratePlantUMLImage generatePlantUMLImage) {
        this.extractKeypointsFromTranscript = extractKeypointsFromTranscript;
        this.generatePlantUMLMindmap = generatePlantUMLMindmap;
        this.generatePlantUMLImage = generatePlantUMLImage;
    }

    /**
     * Builds a graph representing the flow of the agent, including nodes for extracting keypoints from transcripts, generating plantUML mindmaps, and converting mindmaps to images.
     *
     * @return A `StateGraph<State>` instance representing the agent's flow.
     * @throws Exception If any error occurs during the graph construction process.
     */
    public StateGraph<State> buildGraph() throws Exception {
        return new StateGraph<>(State.serializer())
                .addNode("agent", node_async(extractKeypointsFromTranscript))
                .addNode("mindmap", node_async(generatePlantUMLMindmap))
                .addNode("mindmap-to-image", node_async(generatePlantUMLImage))
                .addEdge(START, "agent")
                .addEdge("agent", "mindmap")
                .addEdge("mindmap", "mindmap-to-image")
/*
                .addConditionalEdges(
                        "agent",
                        edge_async( state -> {
                            if (state.agentOutcome().map(AgentOutcome::finish).isPresent()) {
                                return "end";
                            }
                            return "continue";
                        }),
                        Map.of("continue", "action", "end", END)
                )
                .addEdge("action", "agent")
*/
                .addEdge("mindmap-to-image", END)
                ;

    }
}
