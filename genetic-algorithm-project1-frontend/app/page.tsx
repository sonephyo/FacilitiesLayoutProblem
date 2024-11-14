"use client"

import React, {useState} from "react";
import {StompSessionProvider, useStompClient, useSubscription} from "react-stomp-hooks";

export default function Home() {
    const [operationStatus, setOperationStatus] = useState<string>("end");

    return (
        <div>
            <StompSessionProvider
                url={"http://localhost:8080/ws-endpoint"}
            >
                <SubscribingComponent setOperationStatus={setOperationStatus}/>
                <SendingMessages operationStatus={operationStatus}/>
            </StompSessionProvider>
        </div>
    )
}

const SubscribingComponent = ({setOperationStatus}: {
    setOperationStatus: (status: string) => void
}) => {
    const [data, setData] = useState<null | Array<Array<number>>>(null);
    const [affinityValue, setAffinityValue] = useState<number>();
    const [operationNumber, setOperationNumber] = useState<number>();

    const colorPicker = (number: number) => {
        switch (number) {
            case 0:
                return "bg-violet-500"
            case 1:
                return "bg-pink-500"
            case 2:
                return "bg-emerald-300"
            case 3:
                return "bg-red-300"
            case 4:
                return "bg-blue-500"
            default:
                return "bg-gray-800"
        }
    }

    useSubscription("/topic/reply", message => {
        const jsonData = JSON.parse(message.body)

        if ("operationNumber" in jsonData) {
            setOperationNumber(jsonData.operationNumber);
        }

        if ("data" in jsonData) {
            setData(jsonData.data)
        }
        if ("affinity_value" in jsonData) {
            setAffinityValue(jsonData.affinity_value);
        }
    })

    useSubscription("/topic/status", message => {
        const jsonData = JSON.parse(message.body)

        if ("status" in jsonData) {
            setOperationStatus(jsonData.status)
        }
    })
    return (
        <div className={"flex flex-col items-center"}>
            {affinityValue && operationNumber && <>
                <p><span className={"font-bold text-xl"}>Affinity Value: </span>{affinityValue}</p>
                <p><span className={"font-bold text-xl"}>Operation Number: </span>{operationNumber}</p>
            </>}
            {data ? data.map((list, index) => (
                <>
                    <div key={index} className="flex flex-row justify-center">
                        {list.map((item, itemIndex) => (
                            <div key={itemIndex}
                                 className={`w-7 h-7 flex justify-center items-center ${colorPicker(item)} border-2 border-black`}>
                                <p>{item}</p>
                            </div>
                        ))}
                    </div>
                </>

            )) : (
                <div className="flex flex-row justify-center">Operation has not started yet</div>
            )}
        </div>
    )
}

const SendingMessages = ({operationStatus}: {
    operationStatus: string
}) => {
    const [numberOfStations, setNumberOfStations] = useState<number>(48);
    const [numOfThreads, setNumOfThreads] = useState<number>(64);
    const [countOfGAOperations, setCountOfGAOperations] = useState<number>(200);

    const stompClient = useStompClient();

    const start = () => {
        if (stompClient) {
            stompClient.publish({
                destination: "/app/start",
                body: JSON.stringify({
                    numberOfStations: numberOfStations,
                    numberOfThreads: numOfThreads,
                    countOfGAOperations: countOfGAOperations,
                }),
            })
        }
    }

    return (<div className={"flex flex-row justify-center items-center"}>
        <div>
            <div className="flex items-center space-x-4">
                <label className="text-gray-700 font-medium w-1/3" htmlFor="numberOfStations">
                    Number of Stations:
                </label>
                <input
                    className="block w-2/3 p-2 border border-gray-300 rounded-md focus:outline-none focus:ring focus:ring-blue-500"
                    type="number"
                    id="numberOfStations"
                    value={numberOfStations}
                    onChange={(e) => setNumberOfStations(parseInt(e.target.value))}
                    required
                />
            </div>
            <div className="flex items-center space-x-4">
                <label className="text-gray-700 font-medium w-1/3" htmlFor="numberOfThreads">
                    Number of Threads:
                </label>
                <input
                    className="block w-2/3 p-2 border border-gray-300 rounded-md focus:outline-none focus:ring focus:ring-blue-500"
                    type="number"
                    id="numberOfThreads"
                    value={numOfThreads}
                    onChange={(e) => setNumOfThreads(parseInt(e.target.value))}
                    required
                />
            </div>
            <div className="flex items-center space-x-4">
                <label className="text-gray-700 font-medium w-1/3" htmlFor="countOfGAOperations">
                    Count of GA Operations:
                </label>
                <input
                    className="block w-2/3 p-2 border border-gray-300 rounded-md focus:outline-none focus:ring focus:ring-blue-500"
                    type="number"
                    id="countOfGAOperations"
                    value={countOfGAOperations}
                    onChange={(e) => setCountOfGAOperations(parseInt(e.target.value))}
                    required
                />
            </div>
        </div>
        <div className="flex flex-col items-center m-10">
            {operationStatus == "end" ? <button onClick={start}
                                                className={"text-center w-80 bg-blue-500 text-white font-bold py-2 px-4 rounded transition duration-300 ease-in-out transform hover:bg-blue-600 hover:scale-105"}>Start
                Solving FLP Problem
            </button> : <div className={"flex flex-col items-center"}>
                <p>
                    In operation</p>
                <button className={"text-[12px] underline" } onClick={() => {
                    const confirmed = window.confirm("Are you sure you want to stop the operation? Stopping the operation requires the backend server to rerun again.");
                    if (confirmed) {
                        if (stompClient) {
                            stompClient.publish({
                                destination: "/app/terminate"
                            })
                        }
                    }
                }} >Forced End</button>
            </div>}
        </div>
    </div>)
}
