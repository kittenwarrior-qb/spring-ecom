import { z } from 'zod'

// Match backend UserRole
const userRoleSchema = z.union([
  z.literal('ADMIN'),
  z.literal('USER'),
])
export type UserRole = z.infer<typeof userRoleSchema>

// User schema matching backend UserResponse
const userSchema = z.object({
  id: z.number(),
  username: z.string(),
  email: z.string(),
  role: userRoleSchema,
  isEmailVerified: z.boolean(),
  emailVerificationToken: z.string().nullable(),
  emailVerificationTokenExpiry: z.string().nullable(),
  passwordResetToken: z.string().nullable(),
  passwordResetTokenExpiry: z.string().nullable(),
  lastLoginAt: z.string().nullable(),
  isActive: z.boolean(),
  createdAt: z.string(),
  updatedAt: z.string(),
  deletedAt: z.string().nullable(),
  // User info fields (from user_info table)
  firstName: z.string().nullable(),
  lastName: z.string().nullable(),
  phoneNumber: z.string().nullable(),
  dateOfBirth: z.string().nullable(),
  avatarUrl: z.string().nullable(),
  address: z.string().nullable(),
  ward: z.string().nullable(),
  district: z.string().nullable(),
  city: z.string().nullable(),
  postalCode: z.string().nullable(),
})
export type User = z.infer<typeof userSchema>

export const userListSchema = z.array(userSchema)
