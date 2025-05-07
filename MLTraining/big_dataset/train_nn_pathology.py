import os
import pandas as pd
import numpy as np
from sklearn.preprocessing import MinMaxScaler
from sklearn.metrics import accuracy_score, f1_score, precision_score, recall_score
import tensorflow as tf
from utils_data import obtain_evidences, sex, m, f, scaler_path, age, saved_models_dir
import utils_data
import joblib

train_set_path = os.getenv('TRAIN_SET', default='preprocessed_dataset/preprocessed_train_non_diff.csv')
test_set_path = os.getenv('TEST_SET', default='preprocessed_dataset/preprocessed_test_non_diff.csv')
validate_set_path = os.getenv('TEST_SET', default='preprocessed_dataset/preprocessed_validate_non_diff.csv')

def obtain_data(path):
    df = pd.read_csv(path)
    df[sex] = df[sex].map({m: 1, f: 0})
    if not os.path.isdir(saved_models_dir):
        os.makedirs(saved_models_dir)
    if os.path.exists(scaler_path + '.joblib'):
        min_max_scaler = joblib.load(scaler_path + ".joblib")
    else:
        min_max_scaler = MinMaxScaler()
        min_max_scaler.fit(df[['AGE']])
        joblib.dump(min_max_scaler, scaler_path + '.joblib')
    df[age] = min_max_scaler.transform(df[[age]])
    features = [age, sex] + utils_data.obtain_evidences()
    labels = utils_data.obtain_conditions()
    X = df[features].values
    Y = df[labels].values
    return X, Y, len(features), len(labels)

def model_initializer(input_dim, output_dim, metrics, X, Y, epochs=10, batch_size=32):
    model = tf.keras.models.Sequential([
            tf.keras.layers.Input(shape=(input_dim,), name='Input'),
    
            tf.keras.layers.Dense(256, activation='relu'),
            tf.keras.layers.BatchNormalization(),
            tf.keras.layers.Dropout(0.3),

            tf.keras.layers.Dense(128, activation='relu'),
            tf.keras.layers.BatchNormalization(),
            tf.keras.layers.Dropout(0.3),

            tf.keras.layers.Dense(64, activation='relu'),
            tf.keras.layers.BatchNormalization(),
            tf.keras.layers.Dropout(0.3),

            tf.keras.layers.Dense(32, activation='relu'),
            tf.keras.layers.BatchNormalization(),
            tf.keras.layers.Dropout(0.3),
            
            tf.keras.layers.Dense(output_dim, activation='softmax', name='Output')
    ])
    model.compile(
        optimizer=tf.keras.optimizers.Adam(learning_rate=1e-4),
        loss = tf.keras.losses.BinaryCrossentropy(), metrics=metrics
    )
    model.fit(
        X, Y,
        epochs=epochs,
        batch_size=batch_size
    )
    return model

def test_model(model, test_path):
    X_test, Y_test, _, _ = obtain_data(test_path)
    y_true = np.argmax(Y_test, axis=1)
    y_pred = np.argmax(model.predict(X_test), axis=1)
    accuracy = accuracy_score(y_true, y_pred)
    # Accuracy: 0.9832
    # F1-score: 0.9779
    # Precision: 0.9750
    # Recall: 0.9832

    f1 = f1_score(y_true, y_pred, average='weighted')
    precision = precision_score(y_true, y_pred, average='weighted')
    recall = recall_score(y_true, y_pred, average='weighted')

    print(f"Accuracy: {accuracy:.4f}")
    print(f"F1-score: {f1:.4f}")
    print(f"Precision: {precision:.4f}")
    print(f"Recall: {recall:.4f}")

if __name__ == '__main__':
    if len(tf.config.list_physical_devices('GPU')) < 1:
        print('No available GPU found for taining')
        exit(1)
    X_train, Y_train, input_dim, output_dim = obtain_data(train_set_path)
    model = model_initializer(
        input_dim,
        output_dim,
        ['accuracy'],
        X_train,
        Y_train
    )
    print("For test data...")
    test_model(model, test_set_path)
    print("\nFor validate data...")
    test_model(model, validate_set_path)
    model.save("saved_models/neural_network_pathology.keras")
    print("model saved")
    