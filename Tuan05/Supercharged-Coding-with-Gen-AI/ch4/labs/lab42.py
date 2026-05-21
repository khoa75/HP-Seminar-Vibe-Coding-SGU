import random


def run_multiplication_quiz():
    """Run 10 random multiplication quizzes and provide feedback about each to the user."""
    # Fix #2: Set seed for reproducibility of random number generation
    random.seed(42)
    
    # Fix #3: Proper scoping of variable assignments
    correct_count = 0
    total_questions = 10
    
    print("Welcome to the Multiplication Quiz!")
    print("You will be asked 10 multiplication questions.\n")
    
    for i in range(1, total_questions + 1):
        # Generate two random numbers between 1 and 12
        num1 = random.randint(1, 12)
        num2 = random.randint(1, 12)
        correct_answer = num1 * num2
        
        print(f"Question {i}: What is {num1} × {num2}?")
        
        # Fix #4: Error handling for input casting
        try:
            user_answer = int(input("Your answer: "))
            
            if user_answer == correct_answer:
                print("✓ Correct!\n")
                correct_count += 1
            else:
                print(f"✗ Incorrect. The correct answer is {correct_answer}.\n")
        except ValueError:
            print(f"✗ Invalid input. The correct answer was {correct_answer}.\n")
    
    # Provide final feedback
    percentage = (correct_count / total_questions) * 100
    print(f"Quiz completed! You got {correct_count} out of {total_questions} correct ({percentage:.1f}%).")
    
    if percentage >= 90:
        print("Excellent work!")
    elif percentage >= 70:
        print("Good job!")
    elif percentage >= 50:
        print("Not bad, but keep practicing!")
    else:
        print("Keep practicing to improve your skills!")


if __name__ == "__main__":
    # Fix #1: No compilation errors - proper syntax throughout
    run_multiplication_quiz()
