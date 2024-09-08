/* eslint-disable */
import {Module, forwardRef} from '@nestjs/common';
import { MongooseModule } from '@nestjs/mongoose';

import {User, UserSchema} from './model/user.model';
import { UserController } from './user.controller';

@Module({
    imports: [MongooseModule.forFeature([{ name: User.name, schema: UserSchema }]), forwardRef(() => AuthModule)],
    controllers: [UserController],
    
})
export class UserModule {}