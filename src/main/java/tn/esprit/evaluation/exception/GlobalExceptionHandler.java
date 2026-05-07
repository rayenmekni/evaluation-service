package tn.esprit.evaluation.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(EvaluationAlreadyExistsException.class)
    public ResponseEntity<Map<String, String>> handleEvaluationAlreadyExists(EvaluationAlreadyExistsException e) {
        Map<String, String> body = new LinkedHashMap<>();
        body.put("error", "DUPLICATE_EVALUATION");
        body.put("message", e.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(body);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, String>> handleRuntime(RuntimeException e) {
        Map<String, String> body = new LinkedHashMap<>();
        body.put("error", "BAD_REQUEST");
        body.put("message", e.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleGeneric(Exception e) {
        Map<String, String> body = new LinkedHashMap<>();
        body.put("error", "INTERNAL_ERROR");
        body.put("message", e.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);
    }
}
