import openai
import os
from openai import OpenAI
from dotenv import load_dotenv

if __name__ == "__main__":
    
    load_dotenv()
    client: OpenAI = OpenAI(
        api_key=os.getenv("OPENAI_API_KEY")
    )

    completion: openai.ChatCompletion = client.chat.completions.create(
        model="gpt-4o-mini",
        messages=[{"role": "user", "content": "What is the FizzBuzz problem?"}]
    )

    print(completion.choices[0].message.content) 
    
    print("\nToken usage:")
    print("Prompt tokens:", completion.usage.prompt_tokens)
    print("Completion tokens:", completion.usage.completion_tokens)
    print("Total tokens:", completion.usage.total_tokens)