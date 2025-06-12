Activity 1

```markdown
 # Assignment 3 - Activity 1: Simple Server/Client# Assignment 3 - Activity 1: Simple Server/Client

## Project Description

This project implements a simple server/client application using TCP sockets. The server provides four services: add, subtract, multiply, and divide. The client can connect to the server, choose a service, and send requests to perform mathematical operations.

## Protocol Description

The communication between the client and server uses a custom JSON protocol. Each message consists of a header and an optional payload.

### Request Format

```json
{
  "header": {
    "type": "<operation_type>"
  },
  "payload": {
    "num1": <number1>,
    "num2": <number2>
  }
}

```

Where `<operation_type>` can be "add", "subtract", "multiply", or "divide".

### Response Format

```json
 {{
  "header": {
    "type": "response",
    "ok": <boolean>
  },
  "payload": {
    "result": <result>
  }
}

```

### Error Response Format

```json
 {{
  "header": {
    "type": "error",
    "ok": false
  },
  "payload": {
    "message": "<error_message>"
  }
}

```

### Supported Operations

1. Add

1. Request: `{"header": {"type": "add"}, "payload": {"num1": 5, "num2": 3}}`
2. Response: `{"header": {"type": "response", "ok": true}, "payload": {"result": 8}}`



2. Subtract

1. Request: `{"header": {"type": "subtract"}, "payload": {"num1": 10, "num2": 4}}`
2. Response: `{"header": {"type": "response", "ok": true}, "payload": {"result": 6}}`



3. Multiply

1. Request: `{"header": {"type": "multiply"}, "payload": {"num1": 6, "num2": 7}}`
2. Response: `{"header": {"type": "response", "ok": true}, "payload": {"result": 42}}`



4. Divide

1. Request: `{"header": {"type": "divide"}, "payload": {"num1": 20, "num2": 5}}`
2. Response: `{"header": {"type": "response", "ok": true}, "payload": {"result": 4}}`


Error case (division by zero):

1. Request: `{"header": {"type": "divide"}, "payload": {"num1": 10, "num2": 0}}`
2. Response: `{"header": {"type": "error", "ok": false}, "payload": {"message": "Cannot divide by zero"}}`





## Error Handling

The server implements error handling for invalid requests or mathematical errors (e.g., division by zero). If an error occurs, the server sends an error response to the client.

## Unit Tests

Unit tests have been implemented for all four services (add, subtract, multiply, and divide). These tests cover both valid and invalid inputs to ensure robust error handling.

## Hosting Information

The server is hosted on AWS at the following IP address: [Insert your AWS public IP here]

To run the server:

```plaintext
 gradle runServer -Pport=<port_number>gradle runServer -Pport=<port_number>

```

To run the client:

```plaintext
 gradle runClient -Pport=<port_number> -Phost=<host_IP>gradle runClient -Pport=<port_number> -Phost=<host_IP>

```

## Peer Feedback

[Include any peer feedback received here after posting your server information on Slack]

```plaintext
 
Activity 2:

```markdown
# Assignment 3 - Activity 2: Wonder of the World Guessing Game

## Project Description

This project implements a client-server application for a Wonder of the World guessing game. Players connect to the server, which provides image hints of famous world wonders. Players attempt to guess the wonder based on these hints, earning points for correct guesses. The game includes a leaderboard to track high scores.

## Checklist of Requirements

- [x] Simple client implementation
- [x] Server-side game state management
- [x] Client connection and initial greeting
- [x] Main menu with leaderboard, play, and quit options
- [x] Server-side input evaluation
- [x] Game start with user-defined number of rounds
- [x] Guess, skip, next, and remaining hint functionality
- [x] Leaderboard implementation
- [x] Score display at game end
- [x] Return to main menu after game completion
- [x] Robust protocol with error handling
- [x] Overall program robustness and error handling

## Protocol Description

The communication between the client and server uses a custom JSON protocol. Each message consists of a header and an optional payload.

### Request Types

1. Hello
   - Request: `{"type": "hello"}`
   - Response: `{"type": "request_name_age"}`

2. Name and Age
   - Request: `{"type": "name_age", "name": "<player_name>", "age": <player_age>}`
   - Response: `{"type": "greeting", "message": "Welcome, <player_name>!"}`

3. Start Game
   - Request: `{"type": "start", "rounds": <number_of_rounds>}`
   - Response: `{"type": "game_start", "total_rounds": <number_of_rounds>, "current_round": 1, "hint": "<image_path>"}`

4. Guess
   - Request: `{"type": "guess", "guess": "<player_guess>"}`
   - Response (correct): `{"type": "guess_result", "result": "Correct!", "correct": true, "score": <current_score>}`
   - Response (incorrect): `{"type": "guess_result", "result": "Incorrect. Try again!", "correct": false}`

5. Skip
   - Request: `{"type": "skip"}`
   - Response: `{"type": "hint", "hint": "<image_path>", "current_round": <current_round>}`

6. Next Hint
   - Request: `{"type": "next"}`
   - Response: `{"type": "hint", "hint": "<image_path>", "current_round": <current_round>}`

7. Remaining Hints
   - Request: `{"type": "remaining"}`
   - Response: `{"type": "remaining_hints", "remaining": <number_of_remaining_hints>}`

8. Leaderboard
   - Request: `{"type": "leaderboard"}`
   - Response: `{"type": "leaderboard", "leaderboard": [{"name": "<player_name>", "score": <player_score>}, ...]}`

### Error Response

- Response: `{"type": "error", "message": "<error_message>"}`

## Program Design for Robustness

1. Error Handling: Both client and server implement comprehensive error handling to manage network issues, invalid inputs, and unexpected situations.
2. Input Validation: The server validates all incoming requests to ensure they conform to the protocol.
3. Graceful Disconnection: The client and server handle unexpected disconnections gracefully.
4. Timeout Mechanisms: Implemented to prevent indefinite waiting for responses.
5. Logging: Extensive logging is used to track the game state and aid in debugging.

## UDP Considerations

To adapt this program for UDP:

1. Implement a reliability layer to ensure message delivery.
2. Add sequence numbers to messages to handle out-of-order packets.
3. Implement acknowledgment mechanisms for critical game actions.
4. Increase error checking and handling for potential packet loss.
5. Implement a timeout and retransmission strategy for lost packets.

## Video Demonstration

[Insert link to your 4-7 minute screen capture demonstrating the game]

## How to Run

To run the server:

```

gradle runServer -Pport=`<port_number>`

```plaintext
 

To run the client:

```

gradle runClient -Pport=`<port_number>` -Phost=`<host_IP>`

```plaintext
 

Ensure that the `img` folder containing the Wonder of the World images is in the correct location: `Assignment3/Assign3-2/img/`.


```