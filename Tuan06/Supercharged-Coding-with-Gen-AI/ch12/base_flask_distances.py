"""Flask application for calculating distances between vectors.

This module provides a REST API endpoint for computing Manhattan (L1) and Euclidean (L2)
distance metrics between vectors. Part of the Gen-AI Supercharged Coding series.
"""

from flask import Flask, request, jsonify
import numpy as np


app = Flask(__name__)


@app.route("/distances", methods=["POST"])
def calculate_distance():
    """Calculate distance between two matrices using L1 or L2 metrics.
    
    Handles POST requests with JSON payloads containing two matrices and a distance
    metric type. Supports both Manhattan (L1) and Euclidean (L2) distance calculations.
    
    Expected JSON payload:
        {
            "df1": [float, ...] or [[float, ...], ...],  # First matrix/vector
            "df2": [float, ...] or [[float, ...], ...],  # Second matrix/vector
            "distance": "L1" or "L2"                     # Distance metric type
        }
    
    Returns:
        flask.Response: JSON response containing either:
            - {"distance": <float>} if calculation is successful.
            - {"error": <str>} if inputs are invalid or shapes don't match.
    
    Raises:
        No exceptions raised. All errors are returned as JSON error responses.
    """
    data = request.get_json()
    dist_type = data.get("distance")
    if dist_type == "L1":
        a = data.get("df1")
        b = data.get("df2")
        if np.asarray(a).shape != np.asarray(b).shape:
            return jsonify({"error": "Matrices must have the same shape"})
        dist = np.sum(np.abs(a - b))
        return jsonify({"distance": dist})
    elif dist_type == "L2":
        a = data.get("df1")
        b = data.get("df2")
        if np.asarray(a).shape != np.asarray(b).shape:
            return jsonify({"error": "Matrices must have the same shape"})
        dist = 0
        for i in range(len(a)):
            for j in range(len(a[i])):
                dist += (a[i][j] - b[i][j]) ** 2
        dist = np.sqrt(dist)
        return jsonify({"distance": dist})
    else:
        return jsonify({"error": "Invalid distance type"})