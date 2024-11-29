import React, { useRef, useEffect, useState, useMemo } from 'react';
import Quill from 'quill';
import 'quill/dist/quill.snow.css'; // Quill 스타일을 임포트
import axios from 'axios'; // axios 임포트
import './EditorItem.css'; // CSS를 임포트

const Editor = ({ editorContent, setEditorContent, quillRef }) => {
  // const quillRef = useRef(null);   // 사용하는 부모 컴포넌트에서 사용해야하는 것
  // const [editorContent, setEditorContent] = useState(""); // 사용하는 부모 컴포넌트에서 사용해야하는 것

  const editorRef = useRef(null); // Quill을 사용할 DOM 참조
  const [isQuillReady, setIsQuillReady] = useState(false); // Quill 초기화 완료 여부

  // 툴바 옵션 세팅
  const toolbarOptions = useMemo(() => [
    [{ size: ['huge', 'large', false, 'small'] }],
    [{ 'list': 'ordered' }, { 'list': 'bullet' }],
    [{ 'align': [] }],
    ['bold', 'italic', 'underline', 'strike'],
    ['image'],
    [{ 'color': [] }, { 'background': [] }],
  ], []);

  // 이미지 핸들러
  const imageHandler = () => {
    const input = document.createElement('input');
    input.setAttribute('type', 'file');
    input.setAttribute('accept', 'image/*');
    input.click();

    input.addEventListener("change", async () => {
      const file = input.files[0];
      const formData = new FormData();
      formData.append('file', file);

      try {
        // 이미지 저장 요청 보내기
        const response = await axios.post('http://localhost:8080/saveImg', formData, {
          headers: { 'Content-Type': 'multipart/form-data' }
        });

        // 서버에서 반환된 이미지 URL
        const imageUrl = response.data;  // 서버가 imageUrl을 직접 반환한다고 가정

        if (imageUrl) {
          const range = quillRef.current.getSelection();
          if (range) {
            // 이미지 삽입
            quillRef.current.insertEmbed(range.index, 'image', imageUrl);
          }
        }
      } catch (error) {
        console.error('이미지 업로드 실패', error);
      }
    });
  };

  // 모듈 설정(나중에 서버랑 연동할때 모듈 설정)
  const modules = useMemo(() => ({
    toolbar: {
      container: toolbarOptions,
      handlers: {
        'image': imageHandler, // 이미지 버튼 클릭 시에 핸들러 연결
      },
    },
  }), [toolbarOptions]);

  // 스프링이랑 연결하면 위에 모듈 사용해야함
  // const modules = useMemo(() => ({
  //   toolbar: {
  //     container: toolbarOptions,
  //   },
  // }), [toolbarOptions]);

  useEffect(() => {
    if (editorRef.current && !isQuillReady) {
      // Quill 초기화
      const quillInstance = new Quill(editorRef.current, {
        theme: 'snow',
        modules: modules,
      });
      quillRef.current = quillInstance; // Quill 인스턴스를 useRef에 할당
      setIsQuillReady(true); // Quill 초기화 완료

      // 텍스트가 변경될 때마다 HTML 업데이트
      quillInstance.on('text-change', () => {
        const html = quillInstance.root.innerHTML
        setEditorContent(html);
      });
    }

    // 언마운트 시에 editorRef null로 변경
    return () => {
      if (editorRef.current) {
        editorRef.current = null;
      }
    };
  }, [modules, isQuillReady]);

  return (
    <div>
      <div ref={editorRef}></div>
    </div>
  );
};

export default Editor;