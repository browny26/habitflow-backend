from fastapi import FastAPI, HTTPException
from pydantic import BaseModel
from typing import Optional, List, Dict, Any
import os
import requests
import json
import re
import time
import asyncio
from concurrent.futures import ThreadPoolExecutor

app = FastAPI(
    title="AI Habit Service",
    description="Smart AI service that maximizes response preservation",
    version="2.0.0"
)

# Ollama configuration
#OLLAMA_HOST = "http://localhost:11434"
OLLAMA_HOST = os.getenv("OLLAMA_HOST", "http://localhost:11434")
DEFAULT_MODEL = "mistral"

executor = ThreadPoolExecutor(max_workers=4)


# Request Models
class HabitGenerationRequest(BaseModel):
    user_input: str
    difficulty: Optional[str] = "medium"


class AIResponse(BaseModel):
    success: bool
    data: Optional[Dict[str, Any]] = None
    error: Optional[str] = None
    processing_time: float


# Smart Input Classifier
def classify_input_type(user_input: str):
    """
    Determine if input is for a specific habit (single task)
    or a broader goal (multiple tasks)
    """
    user_input_lower = user_input.lower()

    # Patterns that indicate a specific habit (single focused task)
    specific_habit_patterns = [
        r'every (day|morning|evening|night|afternoon)',
        r'each (day|morning|evening|night|afternoon)',
        r'daily',
        r'everyday',
    ]

    # Patterns that indicate a broader goal (multiple tasks)
    broader_goal_patterns = [
        r'learn (.*)',
        r'improve (.*)',
        r'start (.*)',
        r'quit (.*)',
        r'stop (.*)',
        r'make a plan',
        r'create a routine',
        r'create a plan',
        r'create (.*)',
        r'generate (.*)',
        r'achieve (.*)',
        r'reach (.*)'
        r'how to',
        r'want to',
        r'need to',
        r'plan to',
        r'achieve',
        r'reach'
    ]

    # Check for specific habit patterns first
    for pattern in specific_habit_patterns:
        if re.search(pattern, user_input_lower):
            return "specific_habit"

    # Check for broader goal patterns
    for pattern in broader_goal_patterns:
        if re.search(pattern, user_input_lower):
            return "broader_goal"

    # Default to specific habit for simple inputs
    if len(user_input.split()) <= 5:
        return "specific_habit"
    else:
        return "broader_goal"


def extract_habit_name(user_input: str):
    """Extract clean habit name from user input"""
    user_input_lower = user_input.lower()

    # Remove frequency and goal-oriented phrases
    remove_patterns = [
        r'every (day|morning|evening|night|afternoon)',
        r'each (day|morning|evening|night|afternoon)',
        r'daily',
        r'everyday',
        r'\d+\s*times?\s*(a|per|each|every)?\s*(week|month|day)',
        r'for ',
        r'learn ',
        r'improve ',
        r'start ',
        r'quit ',
        r'stop ',
        r'make a plan',
        r'create a routine',
        r'create a ',
        r'plan ',
        r'weekly ',
        r'daily ',
        r'monthly ',
        r'achieve ',
        r'reach '
    ]

    habit_name = user_input_lower
    for pattern in remove_patterns:
        habit_name = re.sub(pattern, '', habit_name, flags=re.IGNORECASE)

    # Clean up
    habit_name = re.sub(r'\s+', ' ', habit_name).strip().title()

    # If empty after cleaning, return original input
    if not habit_name:
        return user_input.title()

    return habit_name


def extract_time_of_day(user_input: str):
    """Extract time of day from user input"""
    user_input_lower = user_input.lower()

    if "morning" in user_input_lower:
        return "morning"
    elif "evening" in user_input_lower:
        return "evening"
    elif "night" in user_input_lower or "bed" in user_input_lower:
        return "night"
    elif "afternoon" in user_input_lower:
        return "afternoon"
    else:
        return None


# Enhanced AI Generation Functions
def generate_with_ollama(prompt: str, model: str = DEFAULT_MODEL, max_retries: int = 2):
    """Generate text using local Ollama API with better error handling"""
    for attempt in range(max_retries):
        try:
            response = requests.post(
                f"{OLLAMA_HOST}/api/generate",
                json={
                    "model": model,
                    "prompt": prompt,
                    "stream": False,
                    "options": {
                        "temperature": 0.7,
                        "top_p": 0.8,
                        "num_predict": 250
                    }
                },
                timeout=120
            )
            response.raise_for_status()
            result = response.json()
            return result.get("response", "")
        except requests.exceptions.RequestException as e:
            if attempt == max_retries - 1:
                print(f"Ollama API error after {max_retries} attempts: {str(e)}")
                return ""
            time.sleep(1)
    return ""


def create_specific_habit_prompt(habit_name: str, time_of_day: str, difficulty: str):
    """Create prompt for specific habits (single focused task)"""
    time_context = f" {time_of_day}" if time_of_day else ""

    return f"""
    Create ONE focused, essential task for {habit_name}{time_context}.
    Difficulty: {difficulty}.

    This should be the single most important action to establish this habit.
    Make it specific, actionable, and achievable.

    Return ONLY a valid JSON object with this structure:
    {{
      "habit_name": "{habit_name}",
      "task_type": "specific_habit",
      "core_task": "single most essential task description",
      "why_important": "brief explanation of why this task is crucial",
      "best_time": "{time_of_day or 'any time'}",
      "estimated_duration": "estimated time needed"
    }}

    Focus on the ONE thing that will make the biggest difference.
    IMPORTANT: Return ONLY the JSON object, no other text.
    """


def create_broader_goal_prompt(goal_description: str, difficulty: str):
    """Create prompt for broader goals (multiple tasks)"""
    return f"""
    Create a comprehensive action plan for: "{goal_description}".
    Difficulty: {difficulty}.

    Provide 5-7 specific, sequential tasks to achieve this goal.
    Make them practical, measurable, and time-bound.

    Return ONLY a valid JSON object with this structure:
    {{
      "goal_name": "cleaned goal name",
      "task_type": "broader_goal",
      "tasks": [
        "specific task 1 description",
        "specific task 2 description",
        "specific task 3 description",
        "specific task 4 description",
        "specific task 5 description"
      ],
      "timeline_suggestions": [
        "suggestion for pacing and timing",
        "milestone recommendation"
      ],
      "key_metrics": [
        "metric 1 to track progress",
        "metric 2 to measure success"
      ]
    }}

    Make the plan realistic and achievable.
    IMPORTANT: Return ONLY the JSON object, no other text.
    Ensure the JSON is complete and properly formatted.
    """


# Enhanced Extraction Functions
def extract_goal_name_from_text(text: str, default_name: str = "Personal Goal"):
    """Extract goal name from text with multiple strategies"""
    # Strategy 1: Look for JSON pattern
    goal_match = re.search(r'"goal_name":\s*"([^"]+)"', text)
    if goal_match:
        return goal_match.group(1)

    # Strategy 2: Look for title-like patterns
    title_patterns = [
        r'for:?\s*"([^"]+)"',
        r'plan for:?\s*([^\n]+)',
        r'goal:?\s*([^\n]+)',
        r'objective:?\s*([^\n]+)'
    ]

    for pattern in title_patterns:
        match = re.search(pattern, text, re.IGNORECASE)
        if match:
            potential_name = match.group(1).strip().strip('"\'')
            if len(potential_name) > 3:
                return potential_name

    # Strategy 3: Context-based naming
    text_lower = text.lower()
    if any(word in text_lower for word in ['study', 'learn', 'education']):
        return "Study Plan"
    elif any(word in text_lower for word in ['exercise', 'workout', 'fitness']):
        return "Fitness Plan"
    elif any(word in text_lower for word in ['diet', 'nutrition', 'eat']):
        return "Nutrition Plan"
    elif any(word in text_lower for word in ['meditate', 'mindfulness']):
        return "Meditation Practice"

    return default_name


def clean_tasks(tasks: List[str]) -> List[str]:
    """Filter out non-task items from tasks list"""
    if not tasks:
        return []

    cleaned_tasks = []
    non_task_patterns = [
        r'suggestion for',
        r'milestone recommendation',
        r'timeline',
        r'metric',
        r'task_type',
        r'key_metrics',
        r'"task_type"',
        r'"key_metrics"',
        r'"timeline_suggestions"',
        r'"goal_name"',
        r'\{.*\}',
        r'\[.*\]',
        r'json',
        r'response',
        r'structure:'
    ]

    for task in tasks:
        task_lower = task.lower()

        # Skip items that match non-task patterns
        if any(pattern in task_lower for pattern in non_task_patterns):
            continue

        # Skip items that are too short or don't look like tasks
        if len(task.strip()) < 10:
            continue

        # Skip items that contain JSON structure keywords
        if any(keyword in task_lower for keyword in ['{', '}', '[', ']', '"']):
            # But allow quoted tasks that are actual tasks
            if not re.search(r'^".+"$', task.strip()):
                continue

        cleaned_tasks.append(task.strip())

    return cleaned_tasks


def extract_tasks_from_text(text: str):
    """Extract tasks from text using multiple strategies"""
    tasks = []

    # Strategy 1: JSON array extraction
    tasks_match = re.search(r'"tasks":\s*\[([\s\S]*?)\]', text)
    if tasks_match:
        tasks_content = tasks_match.group(1)
        # Extract quoted strings from the array
        quoted_tasks = re.findall(r'"([^"]+)"', tasks_content)
        tasks.extend(quoted_tasks)

    # Strategy 2: Look for numbered/bullet items
    lines = text.split('\n')
    for line in lines:
        line = line.strip()

        # Skip JSON structure lines
        if any(keyword in line for keyword in
               ['{', '}', '[', ']', '"goal_name"', '"tasks"', '"timeline"', '"key_metrics"', '"task_type"']):
            continue

        # Look for numbered items (1., 2., etc.)
        numbered_match = re.search(r'^\d+[\.\)]\s*(.+)', line)
        if numbered_match and len(numbered_match.group(1)) > 10:
            tasks.append(numbered_match.group(1))

        # Look for bullet items (-, *, •)
        bullet_match = re.search(r'^[\-\*\•]\s*(.+)', line)
        if bullet_match and len(bullet_match.group(1)) > 10:
            tasks.append(bullet_match.group(1))

        # Look for quoted tasks outside of JSON
        quoted_match = re.search(r'"([^"]+)"', line)
        if quoted_match and len(quoted_match.group(1)) > 10 and 'task' in line.lower():
            tasks.append(quoted_match.group(1))

    # Strategy 3: Action-oriented lines
    action_verbs = ['set', 'prepare', 'review', 'create', 'take', 'ask', 'practice',
                    'study', 'read', 'write', 'complete', 'schedule', 'track', 'measure',
                    'make', 'clean', 'organize', 'wash', 'sweep', 'take out', 'load']

    for line in lines:
        line_lower = line.lower()
        if any(verb in line_lower for verb in action_verbs) and len(line) > 15:
            # Check if it's not already a task and not a JSON structure
            if not any(keyword in line for keyword in ['{', '}', '[', ']']) and line not in tasks:
                tasks.append(line.strip())

    # Clean and deduplicate
    cleaned_tasks = []
    seen = set()
    for task in tasks:
        # Clean the task
        clean_task = re.sub(r'^[\d\-\*\•\.\)\s]+', '', task).strip()
        clean_task = re.sub(r'[,\.]$', '', clean_task).strip()

        if clean_task and clean_task not in seen and len(clean_task) > 8:
            seen.add(clean_task)
            cleaned_tasks.append(clean_task)

    # Filter out non-task items
    filtered_tasks = clean_tasks(cleaned_tasks)

    return filtered_tasks[:7]  # Return max 7 tasks


def extract_timeline_from_text(text: str):
    """Extract timeline suggestions from text"""
    timelines = []

    # JSON extraction
    timeline_match = re.search(r'"timeline_suggestions":\s*\[([\s\S]*?)\]', text)
    if timeline_match:
        timeline_content = timeline_match.group(1)
        quoted_timelines = re.findall(r'"([^"]+)"', timeline_content)
        timelines.extend(quoted_timelines)

    # Pattern-based extraction
    lines = text.split('\n')
    for line in lines:
        line_lower = line.lower()
        if any(keyword in line_lower for keyword in
               ['timeline', 'schedule', 'consistent', 'progress', 'maintain', 'review', 'week', 'month']):
            # Extract meaningful content
            meaningful = re.sub(r'.*(timeline|schedule|consistent|progress|maintain|review)[:\s]*', '', line_lower)
            meaningful = meaningful.strip().capitalize()
            if meaningful and len(meaningful) > 5 and meaningful not in timelines:
                timelines.append(meaningful)

    return timelines[:2] or ["Aim for consistent progress", "Set regular review points"]


def extract_metrics_from_text(text: str):
    """Extract key metrics from text"""
    metrics = []

    # JSON extraction
    metrics_match = re.search(r'"key_metrics":\s*\[([\s\S]*?)\]', text)
    if metrics_match:
        metrics_content = metrics_match.group(1)
        quoted_metrics = re.findall(r'"([^"]+)"', metrics_content)
        metrics.extend(quoted_metrics)

    # Pattern-based extraction
    lines = text.split('\n')
    for line in lines:
        line_lower = line.lower()
        if any(keyword in line_lower for keyword in
               ['metric', 'track', 'measure', 'score', 'progress', 'completion', 'improvement']):
            # Extract meaningful content
            meaningful = re.sub(r'.*(metric|track|measure|score|progress|completion|improvement)[:\s]*', '', line_lower)
            meaningful = meaningful.strip().capitalize()
            if meaningful and len(meaningful) > 5 and meaningful not in metrics:
                metrics.append(meaningful)

    return metrics[:2] or ["Track completion rate", "Measure progress regularly"]


def parse_ai_json_response(response_text: str):
    """Advanced JSON parsing that maximizes AI response preservation"""

    print(f"Raw AI response: '{response_text}'")

    if not response_text.strip():
        return None

    cleaned_text = response_text.strip()

    # Strategy 1: Try to parse as complete JSON
    try:
        json_match = re.search(r'\{[\s\S]*\}', cleaned_text)
        if json_match:
            parsed_data = json.loads(json_match.group())
            # Validate that it has the basic structure we expect
            if isinstance(parsed_data, dict) and ("tasks" in parsed_data or "core_task" in parsed_data):
                return parsed_data
    except json.JSONDecodeError:
        pass  # Continue to other strategies

    # Strategy 2: Try to fix and parse incomplete JSON
    try:
        json_start = cleaned_text.find('{')
        if json_start != -1:
            # Extract potential JSON part
            potential_json = cleaned_text[json_start:]

            # Try to complete the JSON
            completed_json = fix_incomplete_json(potential_json)
            if completed_json:
                parsed_data = json.loads(completed_json)
                if isinstance(parsed_data, dict) and ("tasks" in parsed_data or "core_task" in parsed_data):
                    return parsed_data
    except json.JSONDecodeError:
        pass  # Continue to extraction strategy

    # Strategy 3: Extract individual components from text
    try:
        goal_name = extract_goal_name_from_text(cleaned_text)
        tasks = extract_tasks_from_text(cleaned_text)
        timelines = extract_timeline_from_text(cleaned_text)
        metrics = extract_metrics_from_text(cleaned_text)

        if tasks:  # If we found any tasks, build response
            return {
                "goal_name": goal_name,
                "task_type": "broader_goal",
                "tasks": tasks,
                "timeline_suggestions": timelines,
                "key_metrics": metrics
            }
    except Exception as e:
        print(f"Extraction error: {e}")

    return None

def fix_incomplete_json(json_str: str):
    """Try to fix incomplete JSON responses"""
    if not json_str.strip():
        return None

    # Count braces to check completeness
    open_braces = json_str.count('{')
    close_braces = json_str.count('}')

    # Add missing closing braces
    if open_braces > close_braces:
        json_str += '}' * (open_braces - close_braces)

    # Handle arrays
    open_brackets = json_str.count('[')
    close_brackets = json_str.count(']')

    if open_brackets > close_brackets:
        # Find the last open array and close it
        last_open_bracket = json_str.rfind('[')
        if last_open_bracket != -1:
            # Add closing bracket and remove trailing commas
            json_str = json_str.rstrip(',') + ']'

    # Ensure proper termination
    if not json_str.endswith('}'):
        # Remove trailing commas and add closing brace
        json_str = json_str.rstrip(',') + '}'

    # Validate if it's now parseable
    try:
        test_data = json.loads(json_str)
        return json_str
    except json.JSONDecodeError:
        return None


def generate_fallback_response(input_type: str, habit_name: str, time_of_day: str, difficulty: str):
    """Context-aware fallback response"""
    # Determine goal name based on context
    habit_lower = habit_name.lower()
    if any(word in habit_lower for word in ['study', 'learn', 'education']):
        goal_name = "Study Plan"
        tasks = [
            "Set a regular study schedule with specific time blocks",
            "Create a dedicated, distraction-free study space",
            "Review and organize notes after each study session",
            "Use active recall techniques like flashcards or self-testing",
            "Break down complex topics into smaller, manageable chunks"
        ]
    elif any(word in habit_lower for word in ['exercise', 'workout', 'fitness']):
        goal_name = "Fitness Plan"
        tasks = [
            "Schedule 3-5 weekly workout sessions in your calendar",
            "Prepare workout clothes and equipment the night before",
            "Track your workouts and progress in a fitness app",
            "Include both cardio and strength training exercises",
            "Set specific fitness goals with measurable targets"
        ]
    elif any(word in habit_lower for word in ['diet', 'nutrition', 'eat']):
        goal_name = "Nutrition Plan"
        tasks = [
            "Plan weekly meals with balanced macronutrients",
            "Prepare healthy snacks in advance to avoid temptations",
            "Track daily food intake and water consumption",
            "Include plenty of vegetables and lean proteins in each meal",
            "Limit processed foods and sugary drinks"
        ]
    else:
        goal_name = habit_name or "Personal Goal"
        tasks = [
            f"Define specific, measurable goals for {habit_name}",
            f"Create a weekly schedule for {habit_name} activities",
            f"Gather necessary resources and tools",
            f"Track progress and adjust approach weekly",
            f"Celebrate milestones and stay motivated"
        ]

    if input_type == "specific_habit":
        return {
            "habit_name": habit_name,
            "task_type": "specific_habit",
            "core_task": f"Set aside dedicated time for {habit_name} {time_of_day or 'daily'}",
            "why_important": f"Consistency is key for establishing {habit_name} as a habit",
            "best_time": time_of_day or "consistent daily time",
            "estimated_duration": "10-15 minutes"
        }
    else:
        return {
            "goal_name": goal_name,
            "task_type": "broader_goal",
            "tasks": tasks,
            "timeline_suggestions": [
                "Aim for consistent weekly progress",
                "Set 30-day milestones to measure improvement"
            ],
            "key_metrics": [
                "Weekly consistency rate",
                "Progress toward main objective"
            ]
        }


# Main API Endpoint
@app.post("/api/ai/generate-habit", response_model=AIResponse)
async def generate_habit_plan(request: HabitGenerationRequest):
    """Maximize AI response preservation"""
    start_time = time.time()

    try:
        # Classify input type
        input_type = classify_input_type(request.user_input)
        habit_name = extract_habit_name(request.user_input)
        time_of_day = extract_time_of_day(request.user_input)

        # Create appropriate prompt
        if input_type == "specific_habit":
            prompt = create_specific_habit_prompt(habit_name, time_of_day, request.difficulty)
        else:
            prompt = create_broader_goal_prompt(habit_name, request.difficulty)

        # Generate AI response
        ai_response = await asyncio.get_event_loop().run_in_executor(
            executor, generate_with_ollama, prompt
        )

        # Parse AI response with maximum preservation
        ai_data = parse_ai_json_response(ai_response)

        # Only use minimal fallback if absolutely necessary
        if not ai_data:
            print("Using complete fallback - no AI data could be extracted")
            ai_data = generate_fallback_response(input_type, habit_name, time_of_day, request.difficulty)
        else:
            print("Successfully extracted AI-generated data")

            # Clean tasks if they exist
            if "tasks" in ai_data and ai_data["tasks"]:
                ai_data["tasks"] = clean_tasks(ai_data["tasks"])

            # Preserve AI's data, only add missing essential fields
            if "goal_name" not in ai_data:
                ai_data["goal_name"] = habit_name

            if "task_type" not in ai_data:
                ai_data["task_type"] = input_type

            # Ensure arrays exist and are clean
            if input_type != "specific_habit":
                if "tasks" not in ai_data or not ai_data["tasks"]:
                    fallback = generate_fallback_response(input_type, habit_name, time_of_day, request.difficulty)
                    ai_data["tasks"] = fallback["tasks"][:3]

                if "timeline_suggestions" not in ai_data or not ai_data["timeline_suggestions"]:
                    ai_data["timeline_suggestions"] = ["Aim for consistent progress"]
                else:
                    # Clean timeline suggestions too
                    ai_data["timeline_suggestions"] = [
                        ts for ts in ai_data["timeline_suggestions"]
                        if ts and len(ts) > 5 and "task" not in ts.lower()
                    ]

                if "key_metrics" not in ai_data or not ai_data["key_metrics"]:
                    ai_data["key_metrics"] = ["Track your results regularly"]
                else:
                    # Clean key metrics
                    ai_data["key_metrics"] = [
                        km for km in ai_data["key_metrics"]
                        if km and len(km) > 5 and "task" not in km.lower()
                    ]

        ai_data["detected_input_type"] = input_type

        processing_time = time.time() - start_time

        return AIResponse(
            success=True,
            data=ai_data,
            processing_time=round(processing_time, 2)
        )

    except Exception as e:
        processing_time = time.time() - start_time
        print(f"Unexpected error: {e}")
        return AIResponse(
            success=False,
            error=str(e),
            processing_time=round(processing_time, 2)
        )

# Utility endpoints
@app.get("/api/ai/classify-input")
async def classify_input(user_input: str):
    """Endpoint to test input classification"""
    input_type = classify_input_type(user_input)
    habit_name = extract_habit_name(user_input)
    time_of_day = extract_time_of_day(user_input)

    return {
        "user_input": user_input,
        "detected_type": input_type,
        "extracted_habit": habit_name,
        "time_of_day": time_of_day
    }


@app.get("/api/ai/health")
async def health_check():
    """Health check endpoint"""
    try:
        response = requests.get(f"{OLLAMA_HOST}/api/tags", timeout=5)
        return {"status": "healthy", "ollama": "connected", "models": response.json().get("models", [])}
    except:
        return {"status": "degraded", "ollama": "not_connected"}


@app.get("/")
async def root():
    return {
        "service": "AI Habit Service with Maximum Preservation",
        "version": "2.0.0",
        "description": "Preserves as much AI response as possible with advanced extraction",
        "endpoint": "POST /api/ai/generate-habit"
    }


if __name__ == "__main__":
    import uvicorn

    uvicorn.run(app, host="0.0.0.0", port=8000, timeout_keep_alive=120)