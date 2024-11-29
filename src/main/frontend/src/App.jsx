import EditorItem from "./EditorItem/EditorItem"
import { useRef, useState } from "react";
import './App.css'

function App() {
  const quillRef = useRef(null); // 사용하는 부모 컴포넌트에서 사용
  const [editorContent, setEditorContent] = useState(""); // 사용하는 부모 컴포넌트에서 사용해야하는 것


  const saveToFile = (editorContent) => {
    console.log(editorContent);

    // blob형식의 html파일로 editorContent 저장
    const file = new Blob([editorContent], { type: 'text/html' });

    // 파일업로드요청
    const formData = new FormData();
    formData.append('file', file);

    fetch('http://localhost:8080/saveFile', { method: 'POST', body: formData })
      .then(response => console.log(response)).
      then(() => { alert('파일 업로드 완료!'); })
      .catch(error => console.error('업로드 실패:', error));
  };


  return (
    <>
      <EditorItem quillRef={quillRef} editorContent={editorContent} setEditorContent={setEditorContent} />
      <button onClick={() => saveToFile(editorContent)}>저장</button>
    </>
  )
}

export default App
