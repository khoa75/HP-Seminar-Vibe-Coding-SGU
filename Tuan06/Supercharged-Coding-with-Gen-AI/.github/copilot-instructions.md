---
name: Docstring Standards
description: "Python docstring conventions and generation guidelines for the Supercharged Coding with Gen-AI project. Use when: documenting functions, classes, modules; reviewing code for docstring quality; generating or updating docstrings."
applyTo: "**/*.py"
---

# Python Docstring Standards

## Overview

This project uses **Google-style docstrings** for clarity and consistency across all Python modules. All functions, classes, and modules should include docstrings following the standard format.

## Project Context

- **Project**: Supercharged Coding with Gen-AI (Packt)
- **Scope**: Chapters on Flask APIs, distance calculations, and Gen-AI integration
- **Key Dependencies**: Flask, NumPy, OpenAI, custom utilities

## Docstring Format: Google Style

### Module-Level Docstrings

Place at the beginning of every `.py` file if it contains non-trivial code:

```python
"""Module description in one sentence.

Extended description explaining the module's purpose, main classes/functions,
and how they interact. Include relevant context about domain or use case.
"""
```

**Example:**
```python
"""Flask application for calculating vector distances.

This module provides REST endpoints for computing Manhattan (L1) and Euclidean (L2)
distance metrics between vectors. Part of the Gen-AI Supercharged Coding series.
"""
```

### Function Docstrings

Required for all public functions and methods:

```python
def function_name(arg1, arg2, **kwargs):
    """One-line summary ending with period.
    
    Extended description explaining what the function does, why it's useful,
    and any important context. Break across multiple lines as needed.
    
    Args:
        arg1 (type): Description of arg1.
        arg2 (type): Description of arg2.
        **kwargs: Additional keyword arguments.
    
    Returns:
        return_type: Description of return value and structure.
    
    Raises:
        ExceptionType: When this exception is raised and why.
    """
```

**Example:**
```python
def calculate_distance(vector1, vector2, metric="L2"):
    """Calculate distance between two vectors.
    
    Supports Manhattan (L1) and Euclidean (L2) distance metrics. Both vectors
    must have the same dimensionality.
    
    Args:
        vector1 (list or np.ndarray): First vector coordinates.
        vector2 (list or np.ndarray): Second vector coordinates.
        metric (str): Distance metric, either "L1" or "L2". Default is "L2".
    
    Returns:
        float: Computed distance value.
    
    Raises:
        ValueError: If vectors have different lengths or invalid metric.
    """
```

### Class Docstrings

Required for all classes:

```python
class ClassName:
    """One-line class description.
    
    Extended description of the class, its purpose, and typical usage patterns.
    
    Attributes:
        attr1 (type): Description of instance attribute.
        attr2 (type): Description of another attribute.
    """
    
    def __init__(self, param1, param2):
        """Initialize ClassName.
        
        Args:
            param1 (type): Description of constructor parameter.
            param2 (type): Description of another parameter.
        """
```

**Example:**
```python
class DistanceCalculator:
    """Compute distances between vectors using various metrics.
    
    This class provides a reusable interface for calculating vector distances
    with support for multiple metric types and optional validation.
    
    Attributes:
        metric (str): Active distance metric ("L1" or "L2").
        validate (bool): Whether to validate input vectors.
    """
```

## Key Guidelines

### 1. **Be Specific About Types and Structures**
   - Use actual types: `list`, `np.ndarray`, `dict`, `flask.Response`
   - For complex structures, describe the format:
   
   ```python
   # Good:
   data (dict): JSON payload with keys "df1" (list), "df2" (list), "distance" (str).
   
   # Avoid:
   data: Data dictionary
   ```

### 2. **Explain Parameters and Return Values Clearly**
   - Include expected ranges, constraints, or side effects
   - For Flask endpoints, document expected JSON keys:
   
   ```python
   """Handle distance calculation POST request.
   
   Expects JSON payload:
       {
           "df1": [float, ...],    # First vector
           "df2": [float, ...],    # Second vector
           "distance": "L1"|"L2"   # Metric type
       }
   
   Returns:
       flask.Response: JSON response with calculated distance or error message.
   """
   ```

### 3. **Document Edge Cases and Validations**
   - Mention what happens with invalid inputs
   - List any assumptions about data format or range:
   
   ```python
   Raises:
       ValueError: If vectors have different lengths.
       TypeError: If metric is not "L1" or "L2".
   ```

### 4. **Keep One-Liners Actually One Line**
   - The first line should be a complete, standalone summary
   - Avoid spanning multiple lines if the summary is brief

### 5. **Reference Related Functions/Classes When Relevant**
   ```python
   """Calculate L1 distance.
   
   See also:
       calculate_l2_distance: For Euclidean metric.
       DistanceCalculator: For object-oriented interface.
   """
   ```

## What NOT to Include

- **Implementation details** that belong in code comments, not docstrings
- **TODO or FIXME notes** — use code comments instead
- **Obvious statements** like "Returns nothing" or "Takes no arguments"
- **Deprecated parameter documentation** — remove obsolete parameters instead

## Generation Assistance

When generating or updating docstrings with Gen-AI:

1. **Specify the style**: "Use Google-style docstrings"
2. **Provide context**: Include module purpose and related functions
3. **Request validation**: "Check that all Args and Returns are documented"

## Files Needing Documentation

Priority order:
- [ch12/base_flask_distances.py](../../ch12/base_flask_distances.py) — Flask endpoints
- [ch13/ngrams/ngrams.py](../../ch13/ngrams/ngrams.py) — Text processing
- [ch13/rectangle_intersection/rectangle_intersection.py](../../ch13/rectangle_intersection/rectangle_intersection.py) — Geometry
- [ch14/fibonacci.py](../../ch14/fibonacci.py) — Performance profiling
- [ch15/decorators_openai.py](../../ch15/decorators_openai.py) — Decorator patterns

## Integration with Code Review

- All new functions require docstrings before merge
- Use `/docstring` commands or `/review` to validate quality
- Inconsistent formatting should be standardized in the same commit

## References

- [Google Python Style Guide - Docstrings](https://google.github.io/styleguide/pyguide.html#38-comments-and-docstrings)
- [PEP 257 - Docstring Conventions](https://www.python.org/dev/peps/pep-0257/)
