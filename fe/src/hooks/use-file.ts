import { useMutation } from '@tanstack/react-query'
import { fileApi } from '@/api/file.api'

// Upload file to MinIO - returns presigned URL
export function useFileUpload() {
  return useMutation({
    mutationFn: (file: File) => fileApi.upload(file),
  })
}

// Delete file from MinIO
export function useFileDelete() {
  return useMutation({
    mutationFn: (filename: string) => fileApi.delete(filename),
  })
}
