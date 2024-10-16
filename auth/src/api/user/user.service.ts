/* eslint-disable */

import { Injectable, forwardRef, Inject, Logger } from '@nestjs/common';
import { InjectModel } from '@nestjs/mongoose';
import { FilterQuery, Model } from 'mongoose';
import { User, UserDocument } from './model/user.model';
import { AuthService } from '../auth/auth.service';

@Injectable()
export class UserService {
  logger: Logger;

  constructor(
    @InjectModel(User.name) private userModel: Model<UserDocument>,
    @Inject(forwardRef(() => AuthService)) private AuthService: AuthService,
  ) {
    this.logger = new Logger(UserService.name);
  }

  async findOne(query: any): Promise<any> {
    return await this.userModel.findOne(query);
  }

  async create(user: any): Promise<any> {
    this.logger.log('Creating user.');

    const hashedPassword = await this.AuthService.getHashedPassword(
      user.password,
    );
    user.password = hashedPassword;

    const newUser = new this.userModel(user);
    this.logger.log('User created');
    return newUser.save();
  }

  async findOneAndUpdate(query: any, payload: any): Promise<User> {
    this.logger.log('Updating User');

    return this.userModel.findOneAndUpdate(query, payload, {
      new: true,
      upsert: true,
    });
  }

  async findOneAndRemove(query: any): Promise<User> {
    return this.userModel.findOneAndDelete(query);
  }
}
