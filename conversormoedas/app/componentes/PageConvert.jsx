"use client";

import React, { useState } from "react";
import "./pageConvert.css";
import 'bootstrap/dist/css/bootstrap.min.css';

export default function PageConvert() {
    const [valor, setValor] = useState("");
    const [resultado, setResultado] = useState("");

    const handleChange = (event) => {
        setValor(event.target.value);
    };

    const convertCurrency = async () => {
        try {
            const response = await fetch("http://localhost:8080/convert", {
                method: "POST",
                headers: {
                    "Content-Type": "text/plain",
                },
                body: valor,
            });

            if (response.ok) {
                const data = await response.text();
                setResultado(data);
            } else {
                console.error("Erro ao obter resposta do servidor");
            }
        } catch (error) {
            console.error("Erro ao conectar com o backend:", error);
        }
    };

    return (
        <div>
            <div className="Container">
                <h1 className="Titulo">Convert Currency</h1>
                <div className="DivConversor">
                    <div className="input-group">
                        <span className="input-group-text">R$</span>
                        <input
                            type="text"
                            className="form-control"
                            id="Moeda"
                            value={valor}
                            onChange={handleChange}
                        />
                    </div>
                    <div className="input-group">
                        <input
                            type="text"
                            className="form-control"
                            id="Resultado"
                            value={resultado}
                            readOnly
                        />
                        <span className="input-group-text">$</span>
                    </div>
                </div>
                <button className="btn btn-warning" onClick={convertCurrency}>
                    Converter
                </button>
            </div>
        </div>
    );
}